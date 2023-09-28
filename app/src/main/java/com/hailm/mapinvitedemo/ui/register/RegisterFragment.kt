package com.hailm.mapinvitedemo.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.util.Constants.USERS
import com.hailm.mapinvitedemo.databinding.FragmentRegisterBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
    }

    private val mBinding by viewBinding(FragmentRegisterBinding::bind)
    private val registerViewModel: RegisterViewModel by viewModels()

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {
        with(mBinding) {
            registerViewModel.hasPhoneNumber.observe(viewLifecycleOwner) {
                if (!it) {
                    val phoneNumber = edtPhoneNumber.text.toString().trim()
                    val fullName = edtName.text
                    val gender = edtGender.text

                    val userData = hashMapOf(
                        "phoneNumber" to phoneNumber,
                        "fullName" to "Le Minh Hai",
                        "gender" to "Nam",
                        "avatar" to "dummy link demo",
                        "lat" to "21.037627",
                        "long" to "105.746987"
                    )
                    registerViewModel.addNewUserToFirebase(userData)
                } else {
                    Toast.makeText(context, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            }
            registerViewModel.addNewUser.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                }
            }

            btnRegister.setThrottleClickListener {
                val phoneNumber = edtPhoneNumber.text.toString().trim()
                registerViewModel.checkHasPhoneNumber(phoneNumber)
            }
        }
    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(registerViewModel)
        }
}
