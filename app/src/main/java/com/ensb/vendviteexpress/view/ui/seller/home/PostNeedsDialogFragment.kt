package com.ensb.vendviteexpress.view.ui.seller.home

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.databinding.FragmentPostNeedsDialogBinding
import com.google.android.material.snackbar.Snackbar


class PostNeedsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPostNeedsDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPostNeedsDialogBinding.inflate(inflater, container, false)

        binding.btnPostNeeds.setOnClickListener {
            Snackbar.make(
                requireView(),
                "This feature is coming soon..",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}