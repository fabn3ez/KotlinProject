package com.group5.track.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.group5.track.R

class PropertyDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_property_detail,
            container,
            false
        )

        val btnCall = view.findViewById<Button>(R.id.btnCall)
        val btnBook = view.findViewById<Button>(R.id.btnBook)

        btnCall.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Call button works",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:0712345678")
            )

            startActivity(intent)
        }

        btnBook.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Book button works",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(Intent.ACTION_SENDTO)

            intent.data = Uri.parse("smsto:0712345678")

            intent.putExtra(
                "sms_body",
                "Hi, I am interested in viewing this property."
            )

            startActivity(intent)
        }

        return view
    }
}