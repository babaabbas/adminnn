package com.example.firebase_realtime
import androidx.lifecycle.viewmodel.compose.viewModel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            /*
            val viewModel:CategoryViewModel= viewModel()
            val viewModel2:foodItemViewModel=viewModel()
            val navController= rememberNavController()
            val `food-items` by viewModel.categories.collectAsState()

            val foodItems by viewModel2.foodItems.collectAsState()
            if(foodItems.isNotEmpty()){
                scaff2(navController,`food-items`[0])

            }*/
            myapp()





        }
    }
}



@Composable
fun DeleteFoodItemScreen(viewModel: foodItemViewModel = viewModel()) {
    val foodItems by viewModel.foodItems.collectAsState()

    LazyColumn {
        items(foodItems) { foodItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = foodItem.food_name)
                Button(onClick = { viewModel.deleteFoodItem(foodItem) }) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun UpdateFoodItemScreen(viewModel: foodItemViewModel = viewModel()) {
    val foodItems by viewModel.foodItems.collectAsState()

    var newFoodName by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Update Food Name")

        // TextField to take input
        OutlinedTextField(
            value = newFoodName,
            onValueChange = { newFoodName = it },
            label = { Text("Enter new food name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Update button
        Button(
            onClick = {
                if (foodItems.isNotEmpty()) {
                    val updatedFoodItem = foodItems[0].copy(food_name = newFoodName)
                    viewModel.updateFoodItem(updatedFoodItem)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Update")
        }

        // Display current food items
        LazyColumn {
            items(foodItems) { food ->
                Text(text = "${food.food_name}", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun Display(modifier: Modifier) {
    var categoryData by remember { mutableStateOf<DataSnapshot?>(null) }
    val database = Firebase.database
    val ref = database.getReference("category")

    // Fetch data once
    LaunchedEffect(Unit) {
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                categoryData = snapshot
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (categoryData == null) {
            CircularProgressIndicator() // Show loading indicator
        } else {
            Text(text = categoryData!!.getValue().toString()) // Display data
        }
    }
}

data class Category(
    val cat_name: String = "",
    val id_: String = "",
    val photo_: String = ""
)

data class FoodItem(
    val food_name: String = "",
    val _id: String = "",   // Unique food ID
    val category_id: String = "", // Reference to category
    val isVeg: Boolean = true,
    val calories: Int = 0,
    val model_address: String = "",
    val photo: String = "",
    val rating: Float = 0.0f  // ⭐ Rating out of 5.0
)

class foodItemViewModel: ViewModel(){
    private val _foodItems =  MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> get() = _foodItems
    init{
        fetchFoodItems()
    }
    fun addFoodItem(foodItem: FoodItem) {
        val database = Firebase.database
        val ref = database.getReference("food_items")

        // Generate a unique ID
        val newFoodRef = ref.push()
        val uniqueId = newFoodRef.key ?: return

        // Assign the unique ID to the food item
        val foodWithId = foodItem.copy(_id = uniqueId)

        // Save to Firebase
        newFoodRef.setValue(foodWithId)
            .addOnSuccessListener {
                Log.d("Firebase", "Food item added successfully! ID: $uniqueId")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add food item", e)
            }
    }
    fun fetchFoodItems() {
        val database = Firebase.database
        val ref = database.getReference("food_items")

        // Instead of ref.get() → use addValueEventListener
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val foodItemList = snapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
                    _foodItems.value = foodItemList // Update StateFlow → Compose will refresh
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch food items", error.toException())
            }
        })
    }
    fun updateFoodItem(foodItem: FoodItem) {
        val database = Firebase.database
        val ref = database.getReference("food_items")

        // Use the food item's unique ID to update the existing item
        ref.child(foodItem._id).setValue(foodItem)
            .addOnSuccessListener {
                Log.d("Firebase", "FoodItem updated successfully")
                fetchFoodItems()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to update FoodItem", exception)
            }
    }
    fun deleteFoodItem(foodItem: FoodItem) {
        val database = Firebase.database
        val ref = database.getReference("food_items").child(foodItem._id)

        ref.removeValue().addOnSuccessListener {
            Log.d("Firebase", "Food item deleted successfully")
            // Update the local list after deletion
            _foodItems.value = _foodItems.value.filter { it._id != foodItem._id }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to delete food item", exception)
        }
    }




}
@Composable
fun updateex(viewModel: CategoryViewModel= viewModel()){
    val categories by viewModel.categories.collectAsState()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        categories.getOrNull(1)?.let { category ->
            Button(onClick = {
                val updatedCategory = category.copy(cat_name = "abbas22")
                viewModel.updateCategory(updatedCategory)
            }) {
                Text("Update Category")
            }

            Text(text = "Category Name: ${category.cat_name}")
        } ?: Text("No Category Available")
    }

}
@Composable
fun DeleteCategoryExample(viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()

    LazyColumn {
        items(categories) { category ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(text = category.cat_name)
                Button(onClick = { viewModel.deleteCategory(category) }) {
                    Text("Delete")
                }
            }
        }
    }
}
@Composable
fun CategoryScreen(viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(categories) { category ->
            Text(category.id_)
        }
    }
}
@Composable
fun FootitemScreen(viewModel: foodItemViewModel = viewModel()) {
    val food_items by viewModel.foodItems.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(food_items) { category ->
            Text(category.food_name)
        }
    }
}

class CategoryViewModel:ViewModel(){
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        fetchCategories()
        addCategory(Category("Appetizers","2","3"))

    }
    fun addCategory(category: Category) {
        val database = Firebase.database
        val ref = database.getReference("categories")

        // Generate a unique ID using push()
        val newCategoryRef = ref.push()
        val uniqueId = newCategoryRef.key ?: return  // Get generated ID

        // Assign the unique ID to the category
        val categoryWithId = category.copy(id_ = uniqueId)

        // Save to Firebase
        newCategoryRef.setValue(categoryWithId)
            .addOnSuccessListener {
                Log.d("Firebase", "Category added successfully! ID: $uniqueId")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add category", e)
            }
    }
    fun fetchCategories() {
        val database = Firebase.database
        val ref = database.getReference("categories")

        // Add real-time listener
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val categoryList = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                    _categories.value = categoryList // Update StateFlow → UI reflects automatically
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch categories", error.toException())
            }
        })
    }

    fun updateCategory(updatedCategory: Category) {
        val database = Firebase.database
        val ref = database.getReference("categories").child(updatedCategory.id_)

        ref.setValue(updatedCategory)
            .addOnSuccessListener {
                Log.d("Firebase", "Category updated successfully")
                // Optionally, update local StateFlow
                _categories.value = _categories.value.map { category ->
                    if (category.id_ == updatedCategory.id_) updatedCategory else category
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to update category", exception)
            }
    }
    fun deleteCategory(category: Category) {
        val database = Firebase.database
        val ref = database.getReference("categories").child(category.id_)
        ref.removeValue().addOnSuccessListener {
            // Update local list after deletion
            _categories.value = _categories.value.filter { it.id_ != category.id_ }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to delete category", exception)
        }
    }

}
