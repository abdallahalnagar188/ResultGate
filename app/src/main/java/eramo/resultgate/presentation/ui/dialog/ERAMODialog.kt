package eramo.resultgate.presentation.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import eramo.resultgate.R
import eramo.resultgate.databinding.DialogEramoBinding
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil

class ERAMODialog : DialogFragment(R.layout.dialog_eramo) {
    private lateinit var binding: DialogEramoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogEramoBinding.bind(view)

        binding.apply {
            FDEramoTvWeb.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ERAMO_WEBSITE))
                startActivity(intent)
            }

            FDEramoTvPhone.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse(Constants.ERAMO_PHONE))
                startActivity(callIntent)
            }
        }
    }
}