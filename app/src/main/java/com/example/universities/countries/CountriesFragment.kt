package com.example.universities.countries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universities.MainActivity
import com.example.universities.R
import com.example.universities.countries.CountriesViewModel.Companion.searchTextLiveData
import com.example.universities.databinding.FragmentCountriesBinding
import com.example.universities.universities.UniversitiesFragment

class CountriesFragment : Fragment(), CountryRecyclerAdapter.OnCountryClickListener {

    private var _binding: FragmentCountriesBinding? = null
    private val binding get() = _binding!!
    private val countriesViewModel: CountriesViewModel by activityViewModels()
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        if ((activity as? MainActivity)?.isFirstStart == true) {
            checkSavedCountry()
            (activity as? MainActivity)?.isFirstStart = false
        }

        val adapter = recyclerView?.adapter as? CountryRecyclerAdapter
        val mediatorLiveData = countriesViewModel.getMediatorLiveData()
        mediatorLiveData.observe(viewLifecycleOwner) {
            adapter?.setNewData(it)
        }
        searchViewObserver(adapter)
    }

    private fun searchViewObserver(adapter: CountryRecyclerAdapter?) {
        searchTextLiveData.observe(viewLifecycleOwner) {
            adapter?.filterCountry(countriesViewModel.getFilteredCountries())
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                countriesViewModel.setSearchText(newText.orEmpty())
                return true
            }
        })
    }

    private fun checkSavedCountry() {
        val savedCountry = countriesViewModel.getSharedPrefData()
        if (savedCountry != null) {
            launchFragment(savedCountry)
        }
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.recyclerViewCountry
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = CountryRecyclerAdapter(
            emptyList(),
            this
        )
    }

    override fun onCountryItemClicked(country: String) {
        countriesViewModel.saveCountryToSharedPref(country)
        launchFragment(country)
    }

    private fun launchFragment(country: String) {
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, UniversitiesFragment.newInstance(country))
            .commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView = null
        _binding = null
    }
}