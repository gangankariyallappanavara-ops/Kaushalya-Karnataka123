package com.kaushalyakarnataka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaushalyaTheme {
                KaushalyaApp()
            }
        }
    }
}

private enum class Role { Worker, Customer }
private enum class Screen { Splash, Login, Register, CustomerHome, WorkerDashboard, Search, Bookings, Profile, WorkerProfile, Services, Gallery, Reviews }

private data class AppUser(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: Role,
    val address: String,
    val skill: String = "",
    val experience: Int = 0,
    val bio: String = "",
    val rating: Double = 0.0,
    val totalReviews: Int = 0
)

private data class ServiceItem(val id: String, val workerId: String, val name: String, val price: Int, val description: String)
private data class BookingItem(val id: String, val workerName: String, val serviceName: String, val date: String, val status: String)
private data class ReviewItem(val customerName: String, val rating: Int, val text: String)

private val Orange = Color(0xFFF57C00)
private val Saffron = Color(0xFFFFB300)
private val Green = Color(0xFF2E7D32)
private val Ink = Color(0xFF263238)
private val ScreenBg = Color(0xFFF7F3EC)
private val Categories = listOf("All", "Electrician", "Plumber", "Carpenter", "Painter", "Mechanic")

@Composable
private fun KaushalyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Orange,
            secondary = Green,
            background = ScreenBg,
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Ink,
            onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun KaushalyaApp() {
    val workers = remember {
        mutableStateListOf(
            AppUser("w1", "Ramesh Electric Works", "9876543210", "ramesh@example.com", Role.Worker, "Hubballi, Karnataka", "Electrician", 8, "Wiring, repair, and home electrical service.", 4.7, 32),
            AppUser("w2", "Kiran Plumbing Care", "9123456780", "kiran@example.com", Role.Worker, "Dharwad, Karnataka", "Plumber", 6, "Leak repair, bathroom fitting, and tank cleaning.", 4.5, 21),
            AppUser("w3", "Meena Paint Studio", "9988776655", "meena@example.com", Role.Worker, "Belagavi, Karnataka", "Painter", 10, "Interior, exterior, and wall texture painting.", 4.8, 44),
            AppUser("w4", "Arun Wood Works", "9765432109", "arun@example.com", Role.Worker, "Bengaluru, Karnataka", "Carpenter", 5, "Furniture repair, shelves, doors, and fittings.", 4.3, 16)
        )
    }
    val services = remember {
        mutableStateListOf(
            ServiceItem("s1", "w1", "Home wiring check", 600, "Safety check for switches and wiring."),
            ServiceItem("s2", "w2", "Pipe leak repair", 450, "Quick leak detection and repair."),
            ServiceItem("s3", "w3", "Room repainting", 2500, "One room repainting with basic material.")
        )
    }
    val bookings = remember { mutableStateListOf(BookingItem("b1", "Meena Paint Studio", "Room repainting", "28 May 2026, 10:30 AM", "Pending")) }
    val reviews = remember { mutableStateListOf(ReviewItem("Sahana", 5, "Quick response and clean work."), ReviewItem("Vikram", 4, "Good service and polite communication.")) }

    var screen by remember { mutableStateOf(Screen.Splash) }
    var currentUser by remember { mutableStateOf<AppUser?>(null) }
    var selectedWorker by remember { mutableStateOf(workers.first()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }

    when (screen) {
        Screen.Splash -> SplashScreen { screen = Screen.Login }
        Screen.Login -> LoginScreen(
            onLogin = { role ->
                currentUser = if (role == Role.Worker) workers.first() else AppUser("c1", "Ganga N", "9000000000", "ganga@example.com", Role.Customer, "Karnataka")
                screen = if (role == Role.Worker) Screen.WorkerDashboard else Screen.CustomerHome
            },
            onRegister = { screen = Screen.Register }
        )
        Screen.Register -> RegisterScreen { user ->
            currentUser = user
            if (user.role == Role.Worker) workers.add(user)
            screen = if (user.role == Role.Worker) Screen.WorkerDashboard else Screen.CustomerHome
        }
        Screen.CustomerHome -> CustomerScaffold(screen, { screen = it }) {
            CustomerHomeScreen(
                workers = filteredWorkers(workers, query, selectedCategory),
                selectedCategory = selectedCategory,
                query = query,
                onQuery = { query = it },
                onCategory = { selectedCategory = it },
                onWorker = { selectedWorker = it; screen = Screen.WorkerProfile },
                onSearch = { screen = Screen.Search }
            )
        }
        Screen.Search -> CustomerScaffold(screen, { screen = it }) {
            SearchScreen(workers, query, selectedCategory, onQuery = { query = it }, onCategory = { selectedCategory = it })
        }
        Screen.Bookings -> CustomerScaffold(screen, { screen = it }) { BookingsScreen(bookings) }
        Screen.WorkerProfile -> WorkerProfileScreen(
            worker = selectedWorker,
            services = services.filter { it.workerId == selectedWorker.id },
            reviews = reviews,
            onBack = { screen = Screen.CustomerHome },
            onBook = { service ->
                bookings.add(BookingItem(UUID.randomUUID().toString(), selectedWorker.name, service.name, "30 May 2026, 09:00 AM", "Pending"))
                screen = Screen.Bookings
            }
        )
        Screen.WorkerDashboard -> WorkerScaffold(screen, { screen = it }) {
            WorkerDashboardScreen(currentUser ?: workers.first(), services, reviews)
        }
        Screen.Services -> WorkerScaffold(screen, { screen = it }) {
            ServiceManagementScreen(currentUser ?: workers.first(), services)
        }
        Screen.Gallery -> WorkerScaffold(screen, { screen = it }) { GalleryScreen() }
        Screen.Reviews -> WorkerScaffold(screen, { screen = it }) { ReviewsScreen(reviews) }
        Screen.Profile -> ProfileScreen(currentUser, onLogout = { currentUser = null; screen = Screen.Login })
    }
}

private fun filteredWorkers(workers: List<AppUser>, query: String, category: String): List<AppUser> =
    workers.filter { worker ->
        val matchesCategory = category == "All" || worker.skill == category
        val matchesQuery = query.isBlank() || worker.name.contains(query, true) || worker.skill.contains(query, true)
        matchesCategory && matchesQuery
    }.sortedByDescending { it.rating }

@Composable
private fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onDone()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Saffron, Orange))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Logo(120)
            Spacer(Modifier.height(18.dp))
            Text("Kaushalya Karnataka", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Text("Your Local Skill Network", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            Spacer(Modifier.height(44.dp))
            Text("Loading...", color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun LoginScreen(onLogin: (Role) -> Unit, onRegister: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthFrame(title = "Welcome Back") {
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.take(10).filter(Char::isDigit) },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        if (error.isNotEmpty()) Text(error, color = Color(0xFFC62828), fontSize = 13.sp)
        Button(
            onClick = {
                error = when {
                    !isValidPhone(phone) -> "Enter valid 10-digit mobile number"
                    password.length < 6 -> "Password must be at least 6 characters"
                    else -> ""
                }
                if (error.isEmpty()) onLogin(Role.Customer)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Login as Customer") }
        OutlinedButton(onClick = { onLogin(Role.Worker) }, modifier = Modifier.fillMaxWidth()) { Text("Login as Worker") }
        TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("New User? Register") }
    }
}

@Composable
private fun RegisterScreen(onRegistered: (AppUser) -> Unit) {
    var role by remember { mutableStateOf(Role.Worker) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("Electrician") }
    var experience by remember { mutableFloatStateOf(3f) }
    var error by remember { mutableStateOf("") }

    AuthFrame(title = "Create Account") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = role == Role.Worker, onClick = { role = Role.Worker }, label = { Text("I'm a Worker") })
            FilterChip(selected = role == Role.Customer, onClick = { role = Role.Customer }, label = { Text("I'm a Customer") })
        }
        FormField(name, { name = it }, "Full Name")
        FormField(phone, { phone = it.take(10).filter(Char::isDigit) }, "Phone Number", KeyboardType.Phone)
        FormField(email, { email = it }, "Email", KeyboardType.Email)
        OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        FormField(address, { address = it }, "Address")
        if (role == Role.Worker) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Categories.drop(1)) { item ->
                    FilterChip(selected = skill == item, onClick = { skill = item }, label = { Text(item) })
                }
            }
            Text("Experience: ${experience.toInt()} years", fontWeight = FontWeight.SemiBold)
            Slider(value = experience, onValueChange = { experience = it }, valueRange = 0f..20f, steps = 19)
        }
        if (error.isNotEmpty()) Text(error, color = Color(0xFFC62828), fontSize = 13.sp)
        Button(
            onClick = {
                error = validateRegistration(name, phone, email, password, address)
                if (error.isEmpty()) {
                    onRegistered(
                        AppUser(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            phone = phone,
                            email = email,
                            role = role,
                            address = address,
                            skill = if (role == Role.Worker) skill else "",
                            experience = experience.toInt(),
                            bio = "Available for local skilled service across Karnataka.",
                            rating = 0.0,
                            totalReviews = 0
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Register") }
    }
}

@Composable
private fun AuthFrame(title: String, content: @Composable ColumnScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentPadding = PaddingValues(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Logo(54)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Kaushalya Karnataka", color = Orange, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(18.dp))
        }
        item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerScaffold(current: Screen, onNavigate: (Screen) -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Kaushalya Karnataka", fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar {
                BottomItem("Home", current == Screen.CustomerHome) { onNavigate(Screen.CustomerHome) }
                BottomItem("Search", current == Screen.Search) { onNavigate(Screen.Search) }
                BottomItem("Bookings", current == Screen.Bookings) { onNavigate(Screen.Bookings) }
                BottomItem("Profile", current == Screen.Profile) { onNavigate(Screen.Profile) }
            }
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkerScaffold(current: Screen, onNavigate: (Screen) -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Worker Dashboard", fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar {
                BottomItem("Dashboard", current == Screen.WorkerDashboard) { onNavigate(Screen.WorkerDashboard) }
                BottomItem("Services", current == Screen.Services) { onNavigate(Screen.Services) }
                BottomItem("Gallery", current == Screen.Gallery) { onNavigate(Screen.Gallery) }
                BottomItem("Profile", current == Screen.Profile) { onNavigate(Screen.Profile) }
            }
        },
        content = content
    )
}

@Composable
private fun CustomerHomeScreen(
    workers: List<AppUser>,
    selectedCategory: String,
    query: String,
    onQuery: (String) -> Unit,
    onCategory: (String) -> Unit,
    onWorker: (AppUser) -> Unit,
    onSearch: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            OutlinedTextField(query, onQuery, label = { Text("Search services or workers...") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Button(onClick = onSearch, modifier = Modifier.fillMaxWidth()) { Text("Advanced Search & Filters") }
            CategoryChips(selectedCategory, onCategory)
        }
        items(workers) { worker -> WorkerCard(worker, onWorker) }
    }
}

@Composable
private fun SearchScreen(workers: List<AppUser>, query: String, selectedCategory: String, onQuery: (String) -> Unit, onCategory: (String) -> Unit) {
    var rating by remember { mutableFloatStateOf(3f) }
    val results = filteredWorkers(workers, query, selectedCategory).filter { it.rating >= rating }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            OutlinedTextField(query, onQuery, label = { Text("Worker or service name") }, modifier = Modifier.fillMaxWidth())
            CategoryChips(selectedCategory, onCategory)
            Text("Minimum rating: ${rating.toInt()} stars", fontWeight = FontWeight.SemiBold)
            Slider(rating, { rating = it }, valueRange = 0f..5f, steps = 4)
            Text("Showing ${results.size} workers", color = Green, fontWeight = FontWeight.Bold)
        }
        items(results) { WorkerCard(it) {} }
    }
}

@Composable
private fun WorkerProfileScreen(worker: AppUser, services: List<ServiceItem>, reviews: List<ReviewItem>, onBack: () -> Unit, onBook: (ServiceItem) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            TextButton(onClick = onBack) { Text("Back") }
            ProfileHeader(worker)
            InfoCard("Contact", "${worker.phone}\n${worker.email}\n${worker.address}")
            InfoCard("About", worker.bio)
            Text("Services", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        items(services.ifEmpty { listOf(ServiceItem("demo", worker.id, "${worker.skill} visit", 500, "General local service visit.")) }) { service ->
            CardItem {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(service.name, fontWeight = FontWeight.Bold)
                        Text(service.description, color = Color.DarkGray)
                        Text("Rs. ${service.price}", color = Green, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { onBook(service) }) { Text("Hire") }
                }
            }
        }
        item {
            Text("Reviews", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            reviews.take(3).forEach { ReviewRow(it) }
        }
    }
}

@Composable
private fun WorkerDashboardScreen(user: AppUser, services: List<ServiceItem>, reviews: List<ReviewItem>) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ProfileHeader(user)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Services", services.count { it.workerId == user.id }.toString(), Modifier.weight(1f))
                StatCard("Reviews", reviews.size.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Rating", if (user.rating == 0.0) "New" else "%.1f".format(user.rating), Modifier.weight(1f))
                StatCard("Earnings", "Soon", Modifier.weight(1f))
            }
            Text("Recent Requests", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            InfoCard("Pending booking", "A customer request will appear here when Firebase is connected.")
        }
    }
}

@Composable
private fun ServiceManagementScreen(user: AppUser, services: MutableList<ServiceItem>) {
    var showDialog by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("My Services", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Button(onClick = { showDialog = true }) { Text("+ Add") }
            }
        }
        items(services.filter { it.workerId == user.id }) { service ->
            CardItem {
                Text(service.name, fontWeight = FontWeight.Bold)
                Text("Rs. ${service.price}", color = Green)
                Text(service.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
                TextButton(onClick = { services.remove(service) }) { Text("Delete") }
            }
        }
        if (services.none { it.workerId == user.id }) {
            item { InfoCard("No services added yet", "Tap Add to create your first service card.") }
        }
    }
    if (showDialog) AddServiceDialog(
        onDismiss = { showDialog = false },
        onSave = { name, price, description ->
            services.add(ServiceItem(UUID.randomUUID().toString(), user.id, name, price, description))
            showDialog = false
        }
    )
}

@Composable
private fun GalleryScreen() {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("My Work Gallery", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            InfoCard("Upload photos", "Image picker and Firebase Storage hooks can be connected in the repository layer.")
        }
        items(listOf("Switchboard repair", "Pipe fitting", "Interior painting", "Custom shelf")) { title ->
            CardItem { Text(title, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun ReviewsScreen(reviews: List<ReviewItem>) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Reviews & Ratings", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            InfoCard("Average rating", "4.6 from ${reviews.size} reviews")
        }
        items(reviews) { ReviewRow(it) }
    }
}

@Composable
private fun BookingsScreen(bookings: List<BookingItem>) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Completed", "Cancelled")
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { index, title -> Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) }) }
            }
        }
        items(bookings.filter { it.status.equals(tabs[tab], true) || tab == 0 }) { booking ->
            CardItem {
                Text(booking.workerName, fontWeight = FontWeight.Bold)
                Text(booking.serviceName)
                Text(booking.date, color = Color.DarkGray)
                AssistChip(onClick = {}, label = { Text(booking.status) })
            }
        }
    }
}

@Composable
private fun ProfileScreen(user: AppUser?, onLogout: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ProfileHeader(user ?: AppUser("guest", "Guest User", "9000000000", "guest@example.com", Role.Customer, "Karnataka"))
            listOf("Edit Profile", "Change Password", "Notification Settings", "Help & Support", "Privacy Policy", "Terms & Conditions").forEach {
                CardItem { Text(it, fontWeight = FontWeight.SemiBold) }
            }
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)), modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun AddServiceDialog(onDismiss: () -> Unit, onSave: (String, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField(name, { name = it }, "Service Name")
                FormField(price, { price = it.filter(Char::isDigit) }, "Price", KeyboardType.Number)
                FormField(description, { description = it }, "Description")
            }
        },
        confirmButton = {
            Button(enabled = name.isNotBlank() && price.toIntOrNull() != null, onClick = { onSave(name, price.toInt(), description) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun WorkerCard(worker: AppUser, onClick: (AppUser) -> Unit) {
    CardItem(Modifier.clickable { onClick(worker) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Logo(58)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(worker.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(worker.skill, color = Orange, fontWeight = FontWeight.SemiBold)
                Text("%.1f stars (%d)".format(worker.rating, worker.totalReviews), color = Green)
                Text(worker.address, color = Color.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("View", color = Orange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileHeader(user: AppUser) {
    CardItem {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Logo(74)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                if (user.skill.isNotBlank()) Text("${user.skill} | ${user.experience} years", color = Orange, fontWeight = FontWeight.SemiBold)
                Text(user.address, color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun CategoryChips(selected: String, onCategory: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 10.dp)) {
        items(Categories) { category ->
            FilterChip(selected = selected == category, onClick = { onCategory(category) }, label = { Text(category) })
        }
    }
}

@Composable
private fun RowScope.BottomItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label.take(1), color = if (selected) Orange else Color.DarkGray, fontWeight = FontWeight.Bold)
        Text(label, color = if (selected) Orange else Color.DarkGray, fontSize = 12.sp)
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    CardItem {
        Text(title, fontWeight = FontWeight.Bold)
        Text(body, color = Color.DarkGray)
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    CardItem(modifier) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Orange)
        Text(title, color = Color.DarkGray)
    }
}

@Composable
private fun ReviewRow(review: ReviewItem) {
    CardItem {
        Text(review.customerName, fontWeight = FontWeight.Bold)
        Text("${review.rating} stars", color = Green)
        Text(review.text, color = Color.DarkGray)
    }
}

@Composable
private fun CardItem(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

@Composable
private fun Logo(size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Orange, Saffron))),
        contentAlignment = Alignment.Center
    ) {
        Text("KK", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = (size / 3).sp)
    }
}

@Composable
private fun FormField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun isValidPhone(phone: String): Boolean = phone.length == 10 && phone.all(Char::isDigit) && phone.firstOrNull() in '6'..'9'

private fun validateRegistration(name: String, phone: String, email: String, password: String, address: String): String = when {
    name.length < 3 -> "Name must be at least 3 characters"
    !isValidPhone(phone) -> "Enter valid 10-digit mobile number"
    !email.contains("@") || !email.contains(".") -> "Enter a valid email"
    password.length < 8 -> "Password must be at least 8 characters"
    address.length < 10 -> "Address must be at least 10 characters"
    else -> ""
}
