package co.ke.jamboapps.roadtrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ke.jamboapps.roadtrip.databinding.FragmentMarkRouteAutoBinding

class MarkRouteMapFragment : BaseFragment() {

    private var _binding:FragmentMarkRouteAutoBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMarkRouteAutoBinding.inflate(inflater, container, false)

        return binding.root
    }
}