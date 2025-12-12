package com.vishal.settleup.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val balances = viewModel.balances.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Balances")

        balances.value.forEach { (userId, balance) ->
            Text(text = "$userId : ${balance.netBalance}")
        }
    }
}
