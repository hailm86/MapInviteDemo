package com.hailm.mapinvitedemo.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hailm.mapinvitedemo.base.interfaces.ConsumableEvent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.model.AppAlertDialog
import com.hailm.mapinvitedemo.base.model.AppErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
abstract class BaseFragment(@LayoutRes layoutRes: Int) :
    Fragment(layoutRes), ConsumableEvent {
    companion object {
        @JvmStatic
        private val TAG = BaseFragment::class.java.simpleName
    }

    val context: Context
        @JvmName("getNonNullContext")
        get() = requireContext()

    val activity: FragmentActivity
        @JvmName("getNonNullActivity")
        get() = requireActivity()

    val fragmentManager: FragmentManager
        @JvmName("getNonNullFragmentManager")
        get() = parentFragmentManager

    // Injection

    // Member
    private val progressDialog: ProgressDialog by lazy { ProgressDialog(context) }
    private var mToolbar: Toolbar? = null
    private var mCollapsingToolbarLayout: CollapsingToolbarLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusIconMode()

//        view.findViewById<AppCompatImageButton>(R.id.buttonBack)?.let { buttonBack ->
//            buttonBack.clicks()
//                .debounce(200)
//                .catch { Timber.e(it) }
//                .onEach {
//                    setBackButton()
//                }
//                .launchIn(lifecycleScope)
//        }
//        mToolbar = view.findViewById(R.id.toolbar)
//        mToolbar?.apply {
//            try {
//                val rootDestinations = AppBarConfiguration(
//                    setOf(
//                        R.id.eventsFragment,
//                        R.id.communityFragment,
//                        R.id.profileFragment
//                    )
//                )
//
//                mCollapsingToolbarLayout?.also {
//                    it.setupWithNavController(this, findNavController(), rootDestinations)
//                } ?: setupWithNavController(findNavController(), rootDestinations)
//
//                setOnMenuItemClickListener {
//                    when (it.itemId) {
//                    }
//                    false
//                }
//            } catch (ignored: Exception) {
//            }
//        }

    }

    open fun setBackButton() {
        findNavController().popBackStack()
    }

    open fun setStatusIconMode() {
        val view: View = activity.window.decorView
        view.systemUiVisibility =
            view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelList.forEach {
                    when (it) {
                        is BaseViewModel -> {
                            it.bindLoadingSharedFlow()
                                .onEach {
                                    if (it) {
                                        showProgressDialog()
                                    } else {
                                        hideProgressDialog()
                                    }
                                }.launchIn(this)

                            it.bindErrorSharedFlow()
                                .onEach {
                                    hideSoftKeyboard()
                                    showErrorDialog(it)
                                }
                                .launchIn(this)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    protected fun showProgressDialog() {
        try {
            progressDialog.show()
        } catch (e: WindowManager.BadTokenException) {
            Timber.e(e)
        }

        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.view_progress_dialog)
    }

    protected fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    protected fun showErrorDialog(amErrorDialog: AppErrorDialog) {
        MaterialAlertDialogBuilder(this@BaseFragment.context, R.style.MaterialAlertDialog_Rounded)
            .setTitle(
                when (amErrorDialog.title) {
                    null -> resources.getString(R.string.error)
                    else -> amErrorDialog.title
                }
            )
            .setMessage(amErrorDialog.message)
            .setPositiveButton(R.string.got_it) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    protected fun showAlertDialog(alertDialog: AppAlertDialog, listener: (() -> Unit)?) {
        MaterialAlertDialogBuilder(
            this@BaseFragment.context,
            R.style.MaterialAlertDialog_Rounded_ButtonRed
        )
            .setTitle(
                alertDialog.title
            )
            .setMessage(alertDialog.message)
            .setPositiveButton(
                when (alertDialog.button) {
                    null -> R.string.got_it
                    else -> alertDialog.button
                }
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener?.invoke()
            }
            .setCancelable(alertDialog.isCancel)
            .show()
    }

    protected fun showDecisionDialog(alertDialog: AppAlertDialog, listener: ((Boolean) -> Unit)?) {
        MaterialAlertDialogBuilder(
            this@BaseFragment.context,
            R.style.MaterialAlertDialog_Rounded_ButtonRed
        )
            .setTitle(
                alertDialog.title
            )
            .setMessage(alertDialog.message)
            .setPositiveButton(
                when (alertDialog.button) {
                    null -> R.string.got_it
                    else -> alertDialog.button
                }
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener?.invoke(true)
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener?.invoke(false)
            }
            .setCancelable(alertDialog.isCancel)
            .show()
    }


    protected fun hideSoftKeyboard() {
        activity.currentFocus?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    protected fun showSoftKeyboard() {
        activity.currentFocus?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun setupToolbar(
        rootView: ViewGroup,
        bottomNavigationView: BottomNavigationView?,
        contentView: ViewGroup?,
        windowInsetsCompat: WindowInsetsCompat
    ) {
        val bottomInset =
            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        contentView?.updatePadding(
            bottom = when {
                bottomNavigationView?.isVisible == true -> bottomInset + context.resources.getDimensionPixelSize(
                    R.dimen.default_bottom_navigation_height
                )
                else -> windowInsetsCompat.systemWindowInsetBottom
            }
        )
    }

    protected fun showTitleWhenCollapsed(
        title: String,
        appBar: AppBarLayout,
        toolbarLayout: CollapsingToolbarLayout,
    ) {
        // Show CollapsingToolbarLayout title only when collapsed
        var isShow = true
        var scrollRange = -1
        appBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1) {
                    scrollRange = barLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
//                    toolbarLayout.title = getString(title)
                    toolbarLayout.title = title
                    isShow = true
                } else if (isShow) {
                    toolbarLayout.title = " "
                    isShow = false
                }
            })
    }

    protected fun openWebUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(false)
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.color_sampdoria))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    protected fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
    }
}