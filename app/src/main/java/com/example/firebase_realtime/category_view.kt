package com.example.firebase_realtime

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.firebase_realtime.ui.theme.font3Family

@Composable
fun CategoryInputForm(
    onSubmit: (Category) -> Unit
) {
    var catName by remember { mutableStateOf("") }
    var catId by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    val roundedShape = RoundedCornerShape(28.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Category",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = catName,
            onValueChange = { catName = it },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape
        )

        OutlinedTextField(
            value = catId,
            onValueChange = { catId = it },
            label = { Text("Category ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape
        )

        OutlinedTextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = { Text("Photo URL") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape
        )

        OutlinedButton(
            onClick = {
                val newCategory = Category(
                    cat_name = catName,
                    id_ = catId,
                    photo_ = photoUrl
                )
                onSubmit(newCategory)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = roundedShape
        ) {
            Row(modifier = Modifier, horizontalArrangement = Arrangement.Center){
                Text("Submit")
            }

        }
    }
}


@Composable
fun Real_Home(navController: NavController){
    var isDialogVisible by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier,floatingActionButton = {
        Box(
            modifier = Modifier
                .size(56.dp)  // Size of the circle
                .background(Color.Green, shape = CircleShape)  // Circle background color
                .padding(16.dp).clickable {isDialogVisible=true },  // Padding for the icon inside the circle
            contentAlignment = Alignment.Center
        ){
            Icon(imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier =Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)) )
        }

    },
        floatingActionButtonPosition = FabPosition.End)
    {
            paddingValues ->
        Home(modifier = Modifier,navController,paddingValues)
        if (isDialogVisible) {
            Dialog(onDismissRequest = { isDialogVisible = false
            }) {
                Box(
                    modifier = Modifier
                        .size(300.dp, 400.dp) // Size of the dialog box
                        .clip(RoundedCornerShape(16.dp)) // Rounded corners
                        .background(Color.White) // Background color
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {val viewModel:CategoryViewModel= viewModel()
                    CategoryInputForm {
                        category->viewModel.addCategory(category)
                        viewModel.fetchCategories()
                        isDialogVisible=false
                    }
                }
            }
        }

    }

}

@Composable
fun Home(modifier: Modifier = Modifier, navController: NavController,paddingValues: PaddingValues) {

    Column(modifier = Modifier) {
        Row(
            modifier = Modifier
                .padding(30.dp)
                ,
        )
        {

            Text(
                text = " ADMIN",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 45.sp
                )
            )



        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly)
        {
            val scrollstate= rememberScrollState()
            val viewModel:CategoryViewModel= viewModel()

            val foodCategoryList by viewModel.categories.collectAsState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns in the grid
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = paddingValues
            ) {

                itemsIndexed(foodCategoryList) {
                    index ,item-> rainbowWidget(Color.White, Color.White,item.cat_name,navController,R.drawable.nachos,item)

                }

            }









        }

        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.padding(10.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly)
        {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {}
                    .padding(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {navController.navigate("edit_menu")}
                    .padding(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Search",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {}
                    .padding(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

        }


    }

}

@Composable
fun rainbowWidget(color1:Color,color2:Color,text:String,navController: NavController,path:Int,category: Category){
    var lastItemPaddingEnd = 16.dp
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = lastItemPaddingEnd).clickable { navController.navigate("food/${category.cat_name}/${category.id_}/${category.photo_}")  }
            .shadow(
                elevation = 2.dp, // Shadow elevation
                shape = RoundedCornerShape(25.dp), // Shape of the shadow
                clip = false // Whether to clip content to the shape
            )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .height(150.dp)
                .width(180.dp)
                .background(getGradient(color1, color2))
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id=path),
                contentDescription = "Centered Image",
                modifier = Modifier
                    .size(100.dp) // Adjust size of the image
                ,contentScale = ContentScale.Crop// Background for the image
            )
            Text(
                text = text,
                modifier=Modifier.padding(horizontal =20.dp, vertical = 0.dp),
                fontFamily = font3Family,
                fontSize = 20.sp,
                style = androidx.compose.ui.text.TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
            )


        }
    }

}
fun getGradient(
    startColor: Color,
    endColor: Color,
): Brush {
    return Brush.horizontalGradient(
        colors = listOf(startColor, endColor)
    )
}