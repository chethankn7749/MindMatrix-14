package com.hastakala.shop.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hastakala.shop.activities.MainActivity
import com.hastakala.shop.R
import com.hastakala.shop.databinding.FragmentProfileBinding
import com.hastakala.shop.models.UserEntity
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocaleHelper
import com.hastakala.shop.viewmodels.AuthViewModel
import com.hastakala.shop.viewmodels.AuthViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels {
        val app = requireActivity().application as HastaKalaApplication
        AuthViewModelFactory(app.userRepository, app.sessionManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as HastaKalaApplication
        bindUser(
            UserEntity(
                name = "Creative Artisan",
                email = "artisan@hastakala.com",
                phoneNumber = "+91 98765 43210",
                password = ""
            )
        )
        app.sessionManager.getLoggedInUserId()?.let { uid ->
            app.userRepository.observeUser(uid).observe(viewLifecycleOwner) { user ->
                user?.let(::bindUser)
            }
        }

        binding.languageToggleGroup.check(
            when (LocaleHelper.currentLanguage(requireContext())) {
                "hi" -> R.id.hindiLanguageButton
                "kn" -> R.id.kannadaLanguageButton
                else -> R.id.englishLanguageButton
            }
        )
        binding.languageToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val language = when (checkedId) {
                R.id.hindiLanguageButton -> "hi"
                R.id.kannadaLanguageButton -> "kn"
                else -> "en"
            }
            if (LocaleHelper.currentLanguage(requireContext()) != language) {
                LocaleHelper.persist(requireContext(), language)
                requireActivity().recreate()
            }
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            (requireActivity() as MainActivity).logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindUser(user: UserEntity) {
        binding.profileNameValue.text = user.name
        binding.profileUserNameDetail.text = user.name
        binding.profileEmailDetail.text = user.email
        binding.profilePhoneDetail.text = user.phoneNumber
        val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName ?: "1.0"
        binding.profileVersionValue.text = "${getString(com.hastakala.shop.R.string.app_version)} $versionName"
    }
}
