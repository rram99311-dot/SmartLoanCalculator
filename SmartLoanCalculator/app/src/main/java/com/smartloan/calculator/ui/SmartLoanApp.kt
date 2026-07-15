package com.smartloan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.smartloan.calculator.domain.LoanCalculator
import com.smartloan.calculator.ui.theme.SmartLoanTheme

private object Route { const val SPLASH="splash"; const val HOME="home"; const val EMI="emi"; const val HISTORY="history"; const val SETTINGS="settings"; const val INTEREST="interest"; const val AFFORD="afford" }
@Composable fun SmartLoanApp(vm: LoanViewModel = hiltViewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    SmartLoanTheme(darkTheme = settings.darkMode) { val nav = rememberNavController(); NavHost(nav, Route.SPLASH) {
        composable(Route.SPLASH) { SplashScreen { nav.navigate(Route.HOME) { popUpTo(Route.SPLASH) { inclusive = true } } } }
        composable(Route.HOME) { HomeScreen { nav.navigate(it) } }
        composable(Route.EMI) { EmiScreen(vm) { nav.popBackStack() } }
        composable(Route.HISTORY) { HistoryScreen(vm) { nav.popBackStack() } }
        composable(Route.SETTINGS) { SettingsScreen(settings.currency, settings.darkMode, vm::currency, vm::darkMode) { nav.popBackStack() } }
        composable(Route.INTEREST) { InterestScreen { nav.popBackStack() } }
        composable(Route.AFFORD) { AffordabilityScreen { nav.popBackStack() } }
    } }
}
@Composable private fun SplashScreen(done:()->Unit) { LaunchedEffect(Unit) { delay(650); done() }; Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),contentAlignment=Alignment.Center) { Column(horizontalAlignment=Alignment.CenterHorizontally) { Text("₹",style=MaterialTheme.typography.displayLarge,color=Color.White); Text("Smart Loan",style=MaterialTheme.typography.headlineMedium,color=Color.White,fontWeight=FontWeight.Bold); Text("Plan every payment",color=Color.White.copy(alpha=.8f)) } } }
@Composable private fun HomeScreen(open: (String)->Unit) = Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Smart Loan", fontWeight = FontWeight.Bold) }) }) { pad ->
    val cards = listOf("EMI Calculator" to Route.EMI, "Loan & Principal" to Route.EMI, "Interest Calculator" to Route.INTEREST, "Tenure Calculator" to Route.EMI, "Compare Loans" to Route.EMI, "Affordability" to Route.AFFORD, "Prepayment" to Route.EMI, "Amortization" to Route.EMI, "History" to Route.HISTORY, "Settings" to Route.SETTINGS)
    LazyColumn(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("Plan with confidence", style = MaterialTheme.typography.headlineSmall); Text("Accurate offline calculations for every loan decision.", color = MaterialTheme.colorScheme.onSurfaceVariant) }; items(cards) { (title, route) -> ElevatedCard(Modifier.fillMaxWidth().clickable { open(route) }) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium); Text("›", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary) } } } }
}
@Composable private fun EmiScreen(vm: LoanViewModel, back:()->Unit) { val state by vm.state.collectAsStateWithLifecycle(); Scaffold(topBar={ TopAppBar(title={Text("EMI Calculator")},navigationIcon={TextButton(onClick=back){Text("Back")}}) }, floatingActionButton={ ExtendedFloatingActionButton(onClick=vm::calculate, icon={}, text={Text("Calculate")}) }) { pad -> LazyColumn(Modifier.padding(pad).padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
    item { AmountField("Loan amount", state.amount) { vm.update { s -> s.copy(amount=it) } }; AmountField("Annual interest (%)",state.rate){vm.update{s->s.copy(rate=it)}}; AmountField("Tenure (months)",state.months){vm.update{s->s.copy(months=it)}}; AmountField("Processing fee",state.fee){vm.update{s->s.copy(fee=it)}}; AmountField("Extra monthly payment",state.extra){vm.update{s->s.copy(extra=it)}} }
    state.error?.let { item { Text(it, color=MaterialTheme.colorScheme.error) } }; state.result?.let { r -> item { ResultCard(r.emi, r.totalInterest, r.totalPayment) }; item { LoanChart(r.rows.map { it.interest }, r.rows.map { it.principal }) }; item { Text("Amortization schedule",style=MaterialTheme.typography.titleLarge) }; items(r.rows) { row -> ListItem(headlineContent={Text("Month ${row.month}")}, supportingContent={Text("Principal ${row.principal}  •  Interest ${row.interest}")}, trailingContent={Text("${row.closing}")}); HorizontalDivider() } }
} } }
@Composable private fun AmountField(label:String, value:String, change:(String)->Unit) { OutlinedTextField(value,change,Modifier.fillMaxWidth(),label={Text(label)},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal)) }
@Composable private fun ResultCard(emi:Double, interest:Double, total:Double) = Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) { Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Your monthly EMI",style=MaterialTheme.typography.titleMedium); Text("₹ ${"%.2f".format(emi)}",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold); Text("Total interest: ₹ ${"%.2f".format(interest)}"); Text("Total payment: ₹ ${"%.2f".format(total)}) } }
@Composable private fun LoanChart(interest:List<Double>, principal:List<Double>) { val primary=MaterialTheme.colorScheme.primary; val tertiary=MaterialTheme.colorScheme.tertiary; Card { Column(Modifier.padding(16.dp)) { Text("Principal vs interest",style=MaterialTheme.typography.titleMedium); Canvas(Modifier.fillMaxWidth().height(130.dp).padding(top=12.dp)) { val max=(interest+principal).maxOrNull()?.coerceAtLeast(1.0) ?: 1.0; val step=size.width/(interest.size.coerceAtLeast(1)); interest.forEachIndexed { i,v -> val x=i*step+step/2; drawLine(tertiary,Offset(x,size.height),Offset(x,size.height-(v/max*size.height).toFloat()),step*.55f,StrokeCap.Butt); val p=principal[i]; drawLine(primary,Offset(x,size.height-(v/max*size.height).toFloat()),Offset(x,size.height-((v+p)/max*size.height).toFloat()),step*.55f,StrokeCap.Butt) } } } } }

@Composable private fun HistoryScreen(vm: LoanViewModel, back:()->Unit) { val history by vm.history.collectAsStateWithLifecycle(); Scaffold(topBar={TopAppBar(title={Text("Calculation history")},navigationIcon={TextButton(onClick=back){Text("Back")}})}) { pad -> if(history.isEmpty()) Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){Text("Your saved calculations will appear here.")} else LazyColumn(Modifier.padding(pad)) { items(history,key={it.id}) { item -> ListItem(headlineContent={Text(item.title)},supportingContent={Text("${item.rate}% • ${item.months} months • EMI ₹${item.emi}")},trailingContent={TextButton(onClick={vm.delete(item)}){Text("Delete")}}); HorizontalDivider() } } } }
@Composable private fun SettingsScreen(currency:String, dark:Boolean, setCurrency:(String)->Unit, setDark:(Boolean)->Unit, back:()->Unit) { Scaffold(topBar={TopAppBar(title={Text("Settings")},navigationIcon={TextButton(onClick=back){Text("Back")}})}) { pad -> Column(Modifier.padding(pad).padding(20.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) { Text("Appearance",style=MaterialTheme.typography.titleLarge); Row(verticalAlignment=Alignment.CenterVertically){Text("Dark mode",Modifier.weight(1f)); Switch(dark,setDark)}; Text("Currency",style=MaterialTheme.typography.titleLarge); SingleChoiceSegmentedButtonRow { listOf("INR","USD","EUR","GBP","AED","SAR","JPY").forEachIndexed { index, code -> SegmentedButton(selected=currency==code,onClick={setCurrency(code)},shape=SegmentedButtonDefaults.itemShape(index,7)){Text(code)} } }; Text("All data is stored only on this device.",color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
@Composable private fun InterestScreen(back:()->Unit) { var principal by remember { mutableStateOf("100000") }; var rate by remember { mutableStateOf("8") }; var years by remember { mutableStateOf("5") }; var compound by remember { mutableStateOf(true) }; val amount = runCatching { if(compound) LoanCalculator.compoundAmount(principal.toDouble(),rate.toDouble(),years.toDouble(),12) else principal.toDouble()+LoanCalculator.simpleInterest(principal.toDouble(),rate.toDouble(),years.toDouble()) }.getOrNull(); Scaffold(topBar={TopAppBar(title={Text("Interest Calculator")},navigationIcon={TextButton(onClick=back){Text("Back")}})}) {pad->Column(Modifier.padding(pad).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){AmountField("Principal",principal){principal=it};AmountField("Rate (%)",rate){rate=it};AmountField("Years",years){years=it};Row(verticalAlignment=Alignment.CenterVertically){Text("Compound monthly",Modifier.weight(1f));Switch(compound,{compound=it})};amount?.let{ResultCard(it, it-principal.toDouble(),it)};Text(if(compound) "Formula: A = P(1 + r/n)ⁿᵗ" else "Formula: I = P × r × t")}} }
@Composable private fun AffordabilityScreen(back:()->Unit) { var income by remember { mutableStateOf("100000") }; var expenses by remember { mutableStateOf("35000") }; var existing by remember { mutableStateOf("0") }; val eligible = runCatching { ((income.toDouble()-expenses.toDouble()-existing.toDouble())*0.8).coerceAtLeast(0.0) }.getOrDefault(0.0); Scaffold(topBar={TopAppBar(title={Text("Affordability")},navigationIcon={TextButton(onClick=back){Text("Back")}})}){pad->Column(Modifier.padding(pad).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){AmountField("Monthly income",income){income=it};AmountField("Monthly expenses",expenses){expenses=it};AmountField("Existing EMI",existing){existing=it};ResultCard(eligible,0.0,eligible);Text("Recommended EMI is based on 80% of available monthly cash flow.",color=MaterialTheme.colorScheme.onSurfaceVariant)}} }
