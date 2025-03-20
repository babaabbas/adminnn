package com.example.firebase_realtime

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class FoodItem2(
    val imagePath: Int,   // Path to the food image
    val foodName: String,    // Name of the food item
    val rating: Float,       // Rating of the food item (e.g., out of 5)
    val calorieCount: Int,   // Number of calories in the food item
    val foodType: String     // Type of food, e.g., "Veg" or "Non-Veg"
)

@Composable
fun ImageWidget(size:Int, navController: NavController, destination: String,path:Int) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { navController.navigate(destination) }, // Background color for the screen
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        val path2=R.drawable.stew
        Image(
            painter = painterResource(id=path2),
            contentDescription = "Centered Image",
            modifier = Modifier
                .size(size.dp) // Adjust size of the image
                .shadow(10.dp,shape = RoundedCornerShape(25.dp)
                )
                .background(Color.White, shape = RoundedCornerShape(16.dp))
            ,contentScale = ContentScale.Crop// Background for the image
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun scaff2(navController: NavController,category: Category){
    var searchQuery by remember { mutableStateOf("") }
    val itemnumber = remember { mutableIntStateOf(0) }
    var aror3d by remember { mutableIntStateOf(1) }
    val isSheetVisible = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var number  by remember { mutableIntStateOf(0) }
    var isDialogVisible by remember { mutableStateOf(false) }

    val scrollBehavior= TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    val viewModel:foodItemViewModel= viewModel()
    viewModel.fetchFoodItems()
    val foodItems by viewModel.foodItems.collectAsState()
    val filterditems=foodItems.filter { it.category_id==category.id_ }
    val doublefiltered=filterditems.filter {
        it.food_name.contains(searchQuery, ignoreCase = true)
    }
    Log.d("scaff2","category id is ${category.id_}")
    Log.d("scaff2", "Filtered items count: ${filterditems.size}")
    Spacer(Modifier.height(120.dp))
    Scaffold(modifier = Modifier,
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Category") },
                modifier = Modifier.statusBarsPadding().fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )

        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(56.dp)  // Size of the circle
                    .background(Color.Green, shape = CircleShape)  // Circle background color
                    .padding(16.dp).clickable { isDialogVisible=true },  // Padding for the icon inside the circle
                contentAlignment = Alignment.Center
            ){
                Icon(imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp)) )
            }

        },
        floatingActionButtonPosition = FabPosition.End

    ) {

            paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxHeight(0.96f), contentPadding = PaddingValues(top=paddingValues.calculateTopPadding())){
            itemsIndexed(doublefiltered){ index,foodItem->
                Spacer(modifier= Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable {
                    coroutineScope.launch { isSheetVisible.value = true
                        itemnumber.intValue=index}
                    number=1
                }, horizontalArrangement = Arrangement.Start) {
                    ImageWidget(90,navController, "edit_menu",2)
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp) // Add space between Image and Column
                    ) {
                        // Add any content inside the Column
                        Text(
                            text = foodItem.food_name,

                            style = androidx.compose.ui.text.TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text="Rating:${foodItem.rating}",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)

                        )
                        Text(
                            text="Price:Rs.${foodItem.calories}",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Thin)
                        )
                        var op:String
                        if(foodItem.isVeg){
                            op="veg"
                        }
                        else{
                            op="non-veg"
                        }
                        Text(
                            text = op,
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Thin)

                        )
                    }
                }
                Divider(
                    color = MaterialTheme.colorScheme.errorContainer, // Set the color of the line
                    thickness = 1.dp, // Set the thickness of the line
                    modifier = Modifier.padding(vertical = 8.dp) // Add spacing around the line
                )

            }

        }


    }
    if (isSheetVisible.value) {
        var item:FoodItem=foodItems[itemnumber.value]
        ModalBottomSheet(
            onDismissRequest = { isSheetVisible.value = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Added scrollable functionality
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    ImageWidget(150,navController, "edit_menu",2)
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp) // Add space between Image and Column
                    ) {
                        // Add any content inside the Column
                        Text(
                            text = item.food_name,

                            style = androidx.compose.ui.text.TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text="Rating:${item.rating}",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)

                        )
                        Text(
                            text="Price:Rs.${item.calories}",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Thin)
                        )
                        var op:String
                        if(item.isVeg){
                        op="veg"
                    }
                    else{
                        op="non-veg"
                    }
                        Text(
                            text=op,
                            style = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Thin)

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Icon(
                                painter = painterResource(R.drawable.icon3d) , // Placeholder for AR/3D Icon
                                contentDescription = "See in 3D",
                                modifier = Modifier.size(40.dp).clickable { aror3d=1
                                    isDialogVisible = true},
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                painter = painterResource(R.drawable.iconar), // Placeholder for AR/3D Icon
                                contentDescription = "See in ar",
                                modifier = Modifier.size(40.dp).clickable { navController.navigate("Arscreen")
                                },
                                tint = MaterialTheme.colorScheme.onSurface
                            )


                        }
                    }
                }


                // 3D Food View (Icon for AR/3D viewing)

            }
        }
    }
    if (isDialogVisible) {
        Dialog(onDismissRequest = { isDialogVisible = false
        }) {
            Box(
                modifier = Modifier
                    .size(400.dp, 700.dp) // Size of the dialog box
                    .clip(RoundedCornerShape(16.dp)) // Rounded corners
                    .background(Color.White) // Background color
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                FoodItemInputForm(category) {
                    FoodItem->
                    Toast.makeText(context, "This is a Toast message ${FoodItem.food_name}", Toast.LENGTH_SHORT).show()
                    isDialogVisible=false
                    FoodItem

                }


            }
        }
    }


}
@Composable
fun FoodItemInputForm(category: Category,viewModel: foodItemViewModel= viewModel(),
    onSubmit: (FoodItem) -> FoodItem
) {
    var foodName by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var isVeg by remember { mutableStateOf(true) }
    var calories by remember { mutableStateOf("") }
    var modelAddress by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text("Add Food Item", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(28.dp)
        )

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Food ID") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(28.dp)
        )

        categoryId=category.id_

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {

            if(isVeg){
                Text("Vegetarian")
            }
            else{
                Text("Non-Vegetarian")
            }
            Switch(
                checked = isVeg,
                onCheckedChange = { isVeg = it },
                modifier = Modifier.padding(start = 10.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Green,      // Color when ON
                    uncheckedThumbColor = Color.Red,      // Color when OFF
                    checkedTrackColor = Color.LightGray,  // Track color when ON
                    uncheckedTrackColor = Color.DarkGray  // Track color when OFF
                )
            )

        }

        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it.filter { char -> char.isDigit() } },
            label = { Text("Calories") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(28.dp)
        )

        OutlinedTextField(
            value = modelAddress,
            onValueChange = { modelAddress = it },
            label = { Text("3D Model Address") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(28.dp)
        )

        OutlinedTextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = { Text("Photo URL") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(28.dp)
        )

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it.filter { char -> char.isDigit() || char == '.' } },
            label = { Text("Rating (out of 5)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(28.dp)
        )

        Button(
            onClick = {
                if (foodName.isNotBlank() && id.isNotBlank() && categoryId.isNotBlank()) {
                    val foodItem = FoodItem(
                        food_name = foodName,
                        _id = id,
                        category_id = categoryId,
                        isVeg = isVeg,
                        calories = calories.toIntOrNull() ?: 0,
                        model_address = modelAddress,
                        photo = photoUrl,
                        rating = rating.toFloatOrNull() ?: 0.0f
                    )
                    viewModel.addFoodItem(onSubmit(foodItem))
                    viewModel.fetchFoodItems()

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Submit")
        }
    }
}