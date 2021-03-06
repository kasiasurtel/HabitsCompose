package com.ks.habitscompose.presentation.habits

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ks.habitscompose.presentation.habits.components.HabitItem
import com.ks.habitscompose.presentation.habits.components.OrderSection
import com.ks.habitscompose.presentation.utils.Screen
import com.ks.habitscompose.ui.theme.Cherry
import com.ks.habitscompose.ui.theme.DarkGray
import com.ks.habitscompose.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HabitsScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HabitsScreen(
    navController: NavController,
    viewModel: HabitsViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            if(event) {
                val result = scaffoldState.snackbarHostState.showSnackbar(
                    message = "Habit deleted",
                    actionLabel = "Undo"
                )

                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onEvent(HabitsEvent.RestoreHabit)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.EditHabitScreen.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add habit")
            }
        },
        scaffoldState = scaffoldState
    ) {

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "HABITS", style = Typography.h4)
                IconButton(onClick = { viewModel.onEvent(HabitsEvent.ToggleOrderSection) }) {
                    Icon(
                        imageVector = Icons.Outlined.Sort,
                        contentDescription = "Sort",
                        tint = Cherry
                    )
                }

            }

            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {

                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    habitsOrder = state.habitsOrder,
                    onOrderChange = {
                        viewModel.onEvent(HabitsEvent.Order(it))
                    }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.habits) { habit ->
                    HabitItem(
                        habit = habit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkGray)
//                            .border(
//                                width = 1.dp,
//                                color = Cherry
//                            )
                        ,
                        onEditClick = {
                            viewModel.editedHabit = habit
                            navController.navigate(Screen.EditHabitScreen.route + "?habitId=${habit.id}")
                        },
                        currentWeek = state.weekDays,
                        today = state.today
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }

    }
}