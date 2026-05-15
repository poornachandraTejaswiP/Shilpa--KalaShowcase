package com.example.shilpakalashowcase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage

private val Cream = Color(0xFFFFF8EA)
private val DeepBrown = Color(0xFF4B2E1F)
private val Gold = Color(0xFFD4A017)
private val SoftBrown = Color(0xFF7A5230)
private val CardBg = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ShilpaKalaApp(authViewModel) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShilpaKalaApp(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val artworkViewModel: ArtworkViewModel = viewModel()
    val allArtworks by artworkViewModel.allArtworks.collectAsStateWithLifecycle(initialValue = emptyList())

    MaterialTheme(colorScheme = lightColorScheme(primary = DeepBrown, secondary = Gold, background = Cream)) {
        NavHost(
            navController = navController,
            startDestination = "login",
            enterTransition = { 
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) + fadeIn(tween(500)) 
            },
            exitTransition = { 
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500)) + fadeOut(tween(500)) 
            },
            popEnterTransition = { 
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500)) + fadeIn(tween(500)) 
            },
            popExitTransition = { 
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) + fadeOut(tween(500)) 
            }
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToArtisan = { 
                        navController.navigate("artisan_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToCustomer = {
                        navController.navigate("customer_home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            
            composable("artisan_dashboard") {
                ArtisanDashboardScreen(navController, artworkViewModel)
            }

            composable("customer_home") {
                CustomerHomeScreen(navController, artworkViewModel)
            }

            composable("home") { HomeScreen(navController, artworkViewModel) }
            composable("gallery") { GalleryScreen(navController, artworkViewModel) }
            composable("heritage") { HeritageScreen() }
            composable("profile") { ProfileScreen(artworkViewModel) }
            composable("favorites") { FavoritesScreen(navController, artworkViewModel) }
            composable("about") { AboutScreen() }
            composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                val id = it.arguments?.getString("id") ?: ""
                val art = allArtworks.find { it.id == id }
                if (art != null) {
                    DetailScreen(navController, art, artworkViewModel)
                }
            }
            composable("timeline/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                val id = it.arguments?.getString("id") ?: ""
                val art = allArtworks.find { it.id == id }
                if (art != null) {
                    TimelineScreen(art)
                }
            }
        }
    }
}

@Composable
fun CustomerHomeScreen(navHostController: NavHostController, artworkViewModel: ArtworkViewModel) {
    Scaffold(
        bottomBar = { BottomBar(navHostController) },
        containerColor = Cream
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HomeScreen(navHostController, artworkViewModel)
        }
    }
}

@Composable
fun ArtisanDashboardScreen(navController: NavHostController, artworkViewModel: ArtworkViewModel) = ScreenBase {
    var showDialog by remember { mutableStateOf(false) }
    val artworksDb by artworkViewModel.allArtworks.collectAsStateWithLifecycle(initialValue = emptyList())
    val totalSales by artworkViewModel.totalSales.collectAsStateWithLifecycle(initialValue = "₹0")
    val pendingOrders by artworkViewModel.pendingOrders.collectAsStateWithLifecycle(initialValue = 0)

    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var material by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("Available") }
        var category by remember { mutableStateOf("Stone Idols") }
        
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUrl = it.toString() }
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload New Artwork", fontWeight = FontWeight.Bold, color = DeepBrown) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = material, onValueChange = { material = it }, label = { Text("Material") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (e.g. ₹20,000)") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL or Pick File") }, modifier = Modifier.weight(1f))
                        IconButton(onClick = { launcher.launch("image/*") }) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Image")
                        }
                    }

                    Text("Category", fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Stone Idols", "Wood Carving", "Temple Art").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Status", fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Available", "In Progress", "Sold").forEach { stat ->
                            FilterChip(
                                selected = status == stat,
                                onClick = { status = stat },
                                label = { Text(stat, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            artworkViewModel.insert(
                                Artwork(
                                    id = "SK${System.currentTimeMillis()}",
                                    title = title,
                                    description = description,
                                    material = material,
                                    price = price,
                                    image = imageUrl,
                                    status = status,
                                    artisan = "Ramesh Shilpi",
                                    category = category
                                )
                            )
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBrown)
                ) {
                    Text("Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = DeepBrown)
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Artisan Dashboard", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
        IconButton(onClick = { 
            navController.navigate("login") {
                popUpTo(0)
            }
        }) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = DeepBrown)
        }
    }
    
    Spacer(Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Total Sales: $totalSales", fontWeight = FontWeight.Bold, color = DeepBrown)
            Text("Pending Orders: $pendingOrders", color = SoftBrown)
        }
    }

    Spacer(Modifier.height(24.dp))
    Text("Your Artworks", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
        items(artworksDb) { art ->
            ArtworkCard(art, onFavoriteToggle = { artworkViewModel.toggleFavorite(it) }) { 
                navController.navigate("detail/${art.id}") 
            }
        }
    }
    
    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DeepBrown)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Upload New Artwork")
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        "home" to Icons.Default.Home, 
        "gallery" to Icons.Default.Collections, 
        "heritage" to Icons.Default.MenuBook, 
        "profile" to Icons.Default.Person
    )
    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { (route, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { 
                    navController.navigate(route) { 
                        popUpTo("customer_home") { saveState = true }
                        launchSingleTop = true 
                        restoreState = true
                    } 
                },
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route.replaceFirstChar { it.uppercase() }) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepBrown, 
                    indicatorColor = Gold.copy(.25f)
                )
            )
        }
    }
}

@Composable
fun ScreenBase(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(20.dp),
        content = content
    )
}

@Composable
fun HomeScreen(nav: NavHostController, artworkViewModel: ArtworkViewModel) {
    val filteredArtworks by artworkViewModel.filteredArtworks.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedCategory by artworkViewModel.selectedCategory.collectAsStateWithLifecycle()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Shilpa-Kala Showcase", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
        Text("Ancient Art, Modern Reach", color = SoftBrown, fontSize = 16.sp)
        Spacer(Modifier.height(18.dp))
        if (filteredArtworks.isNotEmpty()) {
            FeatureCard(nav, filteredArtworks.first())
        }
        Spacer(Modifier.height(16.dp))
        Text("Categories", fontWeight = FontWeight.Bold, color = DeepBrown, fontSize = 20.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Stone Idols", "Wood Carving", "Temple Art").forEach { cat -> 
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { artworkViewModel.setCategory(cat) },
                    label = { Text(cat) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { nav.navigate("gallery") }, colors = ButtonDefaults.buttonColors(containerColor = DeepBrown), modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Explore Gallery") }
        TextButton(onClick = { nav.navigate("about") }) { Text("About Project", color = DeepBrown) }
        
        Spacer(Modifier.height(8.dp))
        Text("Results", fontWeight = FontWeight.Bold, color = DeepBrown)
        filteredArtworks.take(4).forEach { art ->
            ArtworkCard(art, onFavoriteToggle = { artworkViewModel.toggleFavorite(it) }) { nav.navigate("detail/${art.id}") }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun FeatureCard(nav: NavHostController, art: Artwork) {
    Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(6.dp), modifier = Modifier.fillMaxWidth().clickable { nav.navigate("detail/${art.id}") }) {
        Box(Modifier.height(230.dp)) {
            AsyncImage(model = art.image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.65f)))))
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                Text("Featured Artwork", color = Gold, fontWeight = FontWeight.Bold)
                Text(art.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GalleryScreen(nav: NavHostController, artworkViewModel: ArtworkViewModel) = Column(Modifier.fillMaxSize().background(Cream).padding(14.dp)) {
    val artworks by artworkViewModel.allArtworks.collectAsStateWithLifecycle(initialValue = emptyList())
    Text("Artwork Gallery", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    Spacer(Modifier.height(10.dp))
    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(artworks) { art -> 
            ArtworkCard(art, onFavoriteToggle = { artworkViewModel.toggleFavorite(it) }) { nav.navigate("detail/${art.id}") } 
        }
    }
}

@Composable
fun ArtworkCard(art: Artwork, onFavoriteToggle: (Artwork) -> Unit, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.clickable(onClick = onClick)) {
        Box {
            Column {
                AsyncImage(model = art.image, contentDescription = art.title, modifier = Modifier.fillMaxWidth().height(140.dp), contentScale = ContentScale.Crop)
                Column(Modifier.padding(10.dp)) {
                    Text(art.title, fontWeight = FontWeight.Bold, color = DeepBrown, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(art.material, color = SoftBrown, fontSize = 12.sp)
                    Text(art.status, color = if (art.status == "Available") Color(0xFF1B7F3A) else if (art.status == "Sold") Color.Red else Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            IconButton(
                onClick = { onFavoriteToggle(art) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.White.copy(0.5f), CircleShape)
            ) {
                Icon(
                    if (art.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (art.isFavorite) Color.Red else DeepBrown
                )
            }
        }
    }
}

@Composable
fun DetailScreen(nav: NavHostController, art: Artwork, viewModel: ArtworkViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Box {
            AsyncImage(model = art.image, contentDescription = art.title, modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(24.dp)).graphicsLayer(scaleX = 1.02f, scaleY = 1.02f), contentScale = ContentScale.Crop)
            IconButton(
                onClick = { viewModel.toggleFavorite(art) },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.White.copy(0.7f), CircleShape)
            ) {
                Icon(
                    if (art.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (art.isFavorite) Color.Red else DeepBrown
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("${art.id} • ${art.status} • ${art.category}", color = Gold, fontWeight = FontWeight.Bold)
        Text(art.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
        Text("Material: ${art.material} | Price: ${art.price}", color = SoftBrown)
        Spacer(Modifier.height(8.dp))
        Text(art.description, color = Color.DarkGray)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { openWhatsApp(context, art) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DeepBrown)) { Text("Enquire on WhatsApp") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { nav.navigate("timeline/${art.id}") }, modifier = Modifier.fillMaxWidth()) { Text("View Process Timeline") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { shareArtwork(context, art) }, modifier = Modifier.fillMaxWidth()) { Text("Share Artwork") }
    }
}

fun openWhatsApp(context: android.content.Context, art: Artwork) {
    val msg = "Hello, I am interested in Artwork ID: ${art.id} - ${art.title}. Please share more details."
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text=${Uri.encode(msg)}"))
    context.startActivity(intent)
}

fun shareArtwork(context: android.content.Context, art: Artwork) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Check this artwork: ${art.title} (${art.id}) - ${art.material}")
    }
    context.startActivity(Intent.createChooser(intent, "Share Artwork"))
}

@Composable
fun TimelineScreen(art: Artwork) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
) {
    Text("Work Progress", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    Text(art.title, color = SoftBrown)
    Spacer(Modifier.height(20.dp))
    
    val steps = if (art.status == "Available" || art.status == "Sold") {
        listOf("Raw Stone Selection", "Rough Carving", "Detailed Carving", "Polishing", "Final Sculpture")
    } else {
        listOf("Raw Stone Selection", "Rough Carving", "Detailed Carving")
    }

    steps.forEachIndexed { i, step ->
        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).clip(CircleShape).background(Gold), contentAlignment = Alignment.Center) { Text("${i + 1}", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Column { Text(step, fontWeight = FontWeight.Bold, color = DeepBrown); Text("Stage completed with traditional hand tools.", color = Color.Gray, fontSize = 13.sp) }
            }
        }
    }
    if (art.status == "In Progress") {
        Text("More stages coming soon...", modifier = Modifier.padding(16.dp), color = SoftBrown)
    }
}

@Composable
fun HeritageScreen() = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
) {
    Text("Heritage Stories", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    Spacer(Modifier.height(16.dp))
    listOf("Hoysala Style" to "Known for detailed temple carvings, floral patterns, and mythological figures.", "Shivarapatna Craft" to "A traditional artisan hub famous for stone idol making.", "Temple Sculpture" to "Preserves Indian cultural identity through handcrafted sacred art.").forEach { (t, d) -> InfoCard(t, d) }
}

@Composable
fun ProfileScreen(viewModel: ArtworkViewModel) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
) {
    val profile by viewModel.artisanProfile.collectAsStateWithLifecycle(initialValue = null)
    var isEditing by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Artist Profile", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
        IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit Profile") }
    }
    
    Spacer(Modifier.height(16.dp))
    
    profile?.let { p ->
        InfoCard(p.name, "Craft Type: ${p.craftType}\nLocation: ${p.location}\nExperience: ${p.experience}\nSpeciality: ${p.speciality}")
        Spacer(Modifier.height(8.dp))
        InfoCard("About Artisan", p.about)

        if (isEditing) {
            var name by remember { mutableStateOf(p.name) }
            var craft by remember { mutableStateOf(p.craftType) }
            var loc by remember { mutableStateOf(p.location) }
            var exp by remember { mutableStateOf(p.experience) }
            var spec by remember { mutableStateOf(p.speciality) }
            var about by remember { mutableStateOf(p.about) }

            AlertDialog(
                onDismissRequest = { isEditing = false },
                title = { Text("Edit Profile") },
                text = {
                    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                        OutlinedTextField(value = craft, onValueChange = { craft = it }, label = { Text("Craft Type") })
                        OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("Location") })
                        OutlinedTextField(value = exp, onValueChange = { exp = it }, label = { Text("Experience") })
                        OutlinedTextField(value = spec, onValueChange = { spec = it }, label = { Text("Speciality") })
                        OutlinedTextField(value = about, onValueChange = { about = it }, label = { Text("About") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateProfile(ArtisanProfile(1, name, craft, loc, exp, spec, about))
                        isEditing = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { isEditing = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun FavoritesScreen(nav: NavHostController, viewModel: ArtworkViewModel) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
) {
    val favorites by viewModel.favoriteArtworks.collectAsStateWithLifecycle(initialValue = emptyList())
    Text("Favorites", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    Spacer(Modifier.height(16.dp))
    if (favorites.isEmpty()) {
        Text("No favorites yet.", color = SoftBrown)
    } else {
        favorites.forEach { art -> 
            ArtworkCard(art, onFavoriteToggle = { viewModel.toggleFavorite(it) }) { 
                nav.navigate("detail/${art.id}") 
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun AboutScreen() = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
) {
    Text("About App", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DeepBrown)
    Spacer(Modifier.height(16.dp))
    InfoCard("Project Vision", "Shilpa-Kala Showcase is a digital gallery for traditional artisans. It connects ancient art with modern buyers through portfolio, timeline, enquiry, and heritage storytelling features.")
}

@Composable
fun InfoCard(title: String, desc: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CardBg), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(16.dp)) { Text(title, fontWeight = FontWeight.Bold, color = DeepBrown, fontSize = 18.sp); Spacer(Modifier.height(4.dp)); Text(desc, color = SoftBrown, fontSize = 14.sp) }
    }
}
