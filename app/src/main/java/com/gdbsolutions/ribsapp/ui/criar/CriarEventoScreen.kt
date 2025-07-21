package com.gdbsolutions.ribsapp.ui.criar


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gdbsolutions.ribsapp.data.local.entity.Adicional
import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import com.gdbsolutions.ribsapp.data.local.entity.Evento
import com.gdbsolutions.ribsapp.ui.theme.LightGrey
import com.gdbsolutions.ribsapp.ui.theme.RibsAppTheme
import com.gdbsolutions.ribsapp.utils.converters.maskedTextToBigDecimal
import com.gdbsolutions.ribsapp.utils.converters.millisParaDataLocalSemFuso
import com.gdbsolutions.ribsapp.utils.converters.textToBigdecimal
import com.gdbsolutions.ribsapp.utils.converters.toStringBR



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CriarEventoScreen(modifier: Modifier = Modifier, viewModel: CriarEventoViewModel) {
    val carnes = viewModel.carnes.observeAsState(emptyList())
    val entradas = viewModel.entradas.observeAsState(emptyList())
    val adicionais = viewModel.adicionais.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.carregarEntradas()
        viewModel.carregarCarnes()
        viewModel.carregarAdicionais()
    }

    var nomeEmpresa by rememberSaveable { mutableStateOf("") }
    var dataEvento by rememberSaveable { mutableStateOf("") }
    var localEvento by rememberSaveable { mutableStateOf("") }
    var numPessoas by rememberSaveable { mutableStateOf("") }
    var precoPorPessoa by rememberSaveable { mutableStateOf("") }
    var kmsRodados by rememberSaveable { mutableStateOf("") }
    var precoPorKm by rememberSaveable { mutableStateOf("") }
    var observacoes by rememberSaveable { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showAdicionarEntrada by remember { mutableStateOf(false) }
    var showAdicionarCarne by remember { mutableStateOf(false) }
    var showAdicionarAdicional by remember { mutableStateOf(false) }
    var showEventoCriado by remember { mutableStateOf(false) }
    val opcoes = rememberSaveable(stateSaver = OpcoesSaver) {
        mutableStateOf(Opcoes())
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Titulo(texto = "Dados do Evento")
            Spacer(modifier = Modifier.height(12.dp))
            SwitchPane(texto = "Nome da Empresa",
                opt = opcoes.value.empresa,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(empresa = novoValor) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            CampoTexto(titulo = "Empresa",
                textoInicial = "Nome da Empresa",
                onValueChange = { nomeEmpresa = it },
                texto = nomeEmpresa, enabled = opcoes.value.empresa)
            Spacer(modifier = Modifier.height(26.dp))

            SwitchPane(texto = "Data do Evento",
                opt = opcoes.value.data,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(data = novoValor) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            CampoTextoData(titulo = "Data do Evento",
                showDatePicker = showDatePicker,
                onValueChange = { dataEvento = it },
                texto = dataEvento,
                enabled = opcoes.value.data)
            Spacer(modifier = Modifier.height(26.dp))

            SwitchPane(texto = "Local/Cidade do Evento",
                opt = opcoes.value.local,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(local = novoValor) })
            Spacer(modifier = Modifier.height(12.dp))
            CampoTexto(
                titulo = "Local/Cidade do Evento",
                textoInicial = "Nome",
                onValueChange = { localEvento = it },
                texto = localEvento,
                enabled = opcoes.value.local
            )
            Spacer(modifier = Modifier.height(26.dp))

            Titulo(texto = "Dados do Churrasco")
            Spacer(modifier = Modifier.height(12.dp))
            SwitchPane(texto = "Entradas",
                opt = opcoes.value.entradas,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(entradas = novoValor)
                    viewModel.alternarAtivacaoGeralEntradas(novoValor)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        listaDeEntradas(
            entradas = entradas.value,
            isSelected = opcoes.value.entradas,
            aoMudarStatus = {index, ativo ->
                viewModel.atualizarStatusEntrada(
                    index,
                    entradas.value,
                    ativo)}
        )
        item {
            Spacer(modifier = Modifier.height(12.dp))
            BotaoAdicionar(texto = "Entrada",
                showAdicionarDialog = showAdicionarEntrada,
                onConfirmar = { nome, descricao ->
                    viewModel.insertEntrada(Entradas(nome = nome, descricao = descricao))
                    viewModel.carregarEntradas()
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            SwitchPane(texto = "Carnes",
                opt = opcoes.value.carnes,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(carnes = novoValor)
                    viewModel.alternarAtivacaoGeralCarnes(novoValor)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        listaDeCarnes(
            carnes = carnes.value,
            isSelected = opcoes.value.carnes,
            aoMudarStatus = {index, ativo ->
                viewModel.atualizarStatusCarne(index, carnes.value, ativo)}
        )
        item {
            Spacer(modifier = Modifier.height(12.dp))
            BotaoAdicionar(texto = "Carne",
                showAdicionarDialog = showAdicionarCarne,
                onConfirmar = { nome, descricao ->
                    viewModel.insertCarne(Carnes(nome = nome, descricao = descricao))
                    viewModel.carregarCarnes()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Titulo(texto = "Precificação")

            Spacer(modifier = Modifier.height(20.dp))
            SwitchPane(texto = "Pessoas",
                opt = opcoes.value.pessoas,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(pessoas = novoValor) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrecosPorText(
                titulo = "nº Pessoas",
                numero = numPessoas,
                onNumeroChange = { numPessoas = it },
                preco = precoPorPessoa,
                onPrecoChange = { precoPorPessoa = it },
                enabled = opcoes.value.pessoas
            )
            Spacer(modifier = Modifier.height(12.dp))
            SwitchPane(texto = "Kilometros Rodados",
                opt = opcoes.value.km,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(km = novoValor) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrecosPorText(titulo = "Kilometros",
                numero = kmsRodados,
                onNumeroChange = { kmsRodados = it },
                preco = precoPorKm,
                onPrecoChange = { precoPorKm = it },
                enabled = opcoes.value.km
            )
            Spacer(modifier = Modifier.height(20.dp))
            SwitchPane(texto = "Adicionais",
                opt = opcoes.value.adicionais,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(adicionais = novoValor)
                    viewModel.alternarAtivacaoGeralAdicionais(novoValor)
                                  },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        listaDeAdicionais(
            adicionais = adicionais.value,
            isSelected = opcoes.value.adicionais,
            aoMudarStatus = {index, ativo ->
                viewModel.atualizarStatusAdicional(index, adicionais.value, ativo)}
        )
        item {
            Spacer(modifier = Modifier.height(12.dp))
            BotaoAdicionais(texto = "Adicionais",
                showAdicionarDialog = showAdicionarAdicional,
                onConfirmar = { nome, descricao, valor ->
                    viewModel.insertAdicional(Adicional(
                        nome = nome,
                        descricao = descricao,
                        valor = valor.textToBigdecimal()
                    ))
                    viewModel.carregarAdicionais()
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Titulo(texto = "Observações")
            Spacer(modifier = Modifier.height(20.dp))
            SwitchPane(texto = "Observações",
                opt = opcoes.value.observacao,
                onCheckedChange = { novoValor ->
                    opcoes.value = opcoes.value.copy(observacao = novoValor) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            CampoTextoMultilinha(titulo = "Observações",
                onValueChange = { observacoes = it },
                valor = observacoes)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val numPessoasLong = if (numPessoas.isNotBlank()) numPessoas.toLong() else 0L
                    val kmsRodadosLong = if (kmsRodados.isNotBlank()) kmsRodados.toLong() else 0L
                    Log.d("TESTE", "Pessoa: ${precoPorPessoa.maskedTextToBigDecimal()} * $numPessoas")
                    Log.d("TESTE", "Km: ${precoPorKm.maskedTextToBigDecimal()} * $kmsRodados")
                        viewModel.criarEventoComRelacoes(
                            Evento(
                                nomeEmpresa = nomeEmpresa.ifBlank { null },
                                dataEvento = dataEvento.ifBlank { null },
                                localEvento = localEvento.ifBlank { null },
                                numPessoas = numPessoasLong,
                                precoPorPessoa = precoPorPessoa.maskedTextToBigDecimal(),
                                kmsRodados = kmsRodadosLong,
                                precoPorKm = precoPorKm.maskedTextToBigDecimal(),
                                observacoes = observacoes
                            ),
                            carnes.value.filter { it.ativo },
                            entradas.value.filter { it.ativo },
                            adicionais.value.filter { it.ativo }
                        )
                    showEventoCriado = true
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Localized description"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Criar Evento",
                        textAlign = TextAlign.Start
                        )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (showEventoCriado) {
                EventoCriadoDialog(onDismiss = { showEventoCriado = false })
            }
        }
    }
}

fun LazyListScope.listaDeEntradas(
    entradas: List<Entradas>,
    aoMudarStatus: (index: Int, ativo: Boolean) -> Unit,
    isSelected: Boolean
) {
    items(entradas.size) { index ->
        CardOpcao(
            nome = entradas[index].nome,
            descricao = entradas[index].descricao,
            checked = entradas[index].ativo,
            onCheckedChange = { novoValor ->
                if (isSelected) {
                    aoMudarStatus(index, novoValor)
                }
            },
            enabled = isSelected
        )
    }
}

fun LazyListScope.listaDeCarnes(
    carnes: List<Carnes>,
    aoMudarStatus: (index: Int, ativo: Boolean) -> Unit,
    isSelected: Boolean
) {
    items(carnes.size) { index ->
        CardOpcao(nome = carnes[index].nome,
            descricao = carnes[index].descricao,
            checked = carnes[index].ativo,
            onCheckedChange = { novoValor ->
                if (isSelected) {
                    aoMudarStatus(index, novoValor)
                }

            },
            enabled = isSelected
        )
    }
}
fun LazyListScope.listaDeAdicionais(
    adicionais: List<Adicional>,
    aoMudarStatus: (index: Int, ativo: Boolean) -> Unit,
    isSelected: Boolean
) {
    items(adicionais.size) { index ->
        CardOpcao(nome = adicionais[index].nome,
            descricao = adicionais[index].descricao,
            isAdicionais = true,
            valor = adicionais[index].valor.toStringBR(),
            checked = adicionais[index].ativo,
            onCheckedChange = {novoValor ->
                if (isSelected) {
                    aoMudarStatus(index, novoValor)
                }
                },
            enabled = isSelected
        )
    }
}

@Composable
fun Titulo(modifier: Modifier = Modifier, texto: String) {
    Column(modifier = modifier) {
        Text(
            text = texto,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start
        )
        Linha(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun Linha(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth()) {
        drawLine(
            color = LightGrey,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 10f,
        )
    }
}

@Composable
fun CampoTextoMultilinha(
    valor: String,
    onValueChange: (String) -> Unit,
    titulo: String = "Obeervações",
    linhas: Int = 5
) {
    TextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(titulo) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        maxLines = linhas,
        singleLine = false
    )
}

@Composable
fun SwitchPane(
    modifier: Modifier = Modifier,
    texto: String,
    opt: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Switch(
            checked = opt,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
fun CardOpcao(
    modifier: Modifier = Modifier,
    nome: String,
    descricao: String? = null,
    isAdicionais: Boolean = false,
    valor: String? = null,
    checked: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val cardAlpha = if (checked) 1f else 0.4f
    val backgroundColor = if (checked)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (checked)
        MaterialTheme.colorScheme.background
    else
        MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .alpha(if(enabled) cardAlpha else 0.3f),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.height(20.dp)
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = if (enabled) onCheckedChange else null,
                    modifier = Modifier.padding(end = 8.dp, top = 16.dp)
                )
                Text(
                    text = nome,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    textAlign = TextAlign.Start,
                )
                if (isAdicionais) {
                    Text(
                        text = "R$ $valor",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                }
            }
            if (descricao != null) {
                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 58.dp, bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CampoTexto(modifier: Modifier = Modifier,
               texto: String = "",
               titulo: String,
               textoInicial: String,
               onValueChange: (String) -> Unit = {},
               enabled: Boolean = true) {
    TextField(
        value = texto,
        onValueChange = onValueChange,
        placeholder = { Text(textoInicial) },
        label = { Text(titulo) },
        enabled = enabled,
        trailingIcon = {
            IconButton(
                onClick = {onValueChange("")}
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Localized description"
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CampoTextoData(
    modifier: Modifier = Modifier,
    titulo: String,
    showDatePicker: Boolean,
    onValueChange: (String) -> Unit = {},
    texto: String,
    enabled: Boolean = true
) {
    var showDate by remember { mutableStateOf(showDatePicker) }
    TextField(
        value = texto,
        onValueChange = {},
        label = { Text(titulo) },
        enabled = enabled,
        trailingIcon = {
            IconButton(
                onClick = {
                    showDate = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Localized description",
                )
            }
        },
        readOnly = true,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
    if (showDate) {
        DatePickerModal(
            onDateSelected = {
                if (it != null) {
                    onValueChange(it.millisParaDataLocalSemFuso())
                }
            },
            onDismiss = {
                showDate = false
            }
        )
    }
}

@Composable
fun CampoTextoNumerico(
    modifier: Modifier = Modifier,
    texto: String = "",
    titulo: String,
    textoInicial: String,
    onValueChange: (String) -> Unit = {},
    isPreco: Boolean = false,
    enabled: Boolean = true
) {
    TextField(
        value = texto,
        onValueChange = onValueChange,
        placeholder = { Text(textoInicial) },
        label = { Text(titulo) },
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Limpar"
                )
            }
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number // <- AQUI ativa o teclado numérico
        ),
        visualTransformation = if(isPreco) DecimalMaskTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun CampoTextoObrigatorio(
    valor: String,
    onValueChanged: (String) -> Unit,
    titulo: String,
    mostrarErro: Boolean
) {
    TextField(
        value = valor,
        onValueChange = onValueChanged,
        label = { Text(titulo) },
        isError = mostrarErro,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    if (mostrarErro) {
        Text(
            text = "Campo obrigatório",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

class DecimalMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        val formatted = when {
            digits.isEmpty() -> "0,00"
            digits.length == 1 -> "0,0$digits"
            digits.length == 2 -> "0,$digits"
            else -> {
                val reais = digits.dropLast(2)
                val centavos = digits.takeLast(2)
                "${reais.toLong()},$centavos"
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return digits.length
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}


@Composable
fun BotaoAdicionar(texto: String, showAdicionarDialog: Boolean, onConfirmar: (String, String) -> Unit) {
    var showDialog by remember { mutableStateOf(showAdicionarDialog) }
    Button(
        onClick = {
            showDialog = true
        },
        colors = ButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        )
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Add,
                tint = Color.White,
                contentDescription = "Localized description",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = "Adicionar $texto",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
    if (showDialog) {
        AdicionarAlgoDialog(tipo = texto, onDismiss = { showDialog = false }, onConfirmar = onConfirmar)
    }
}

@Composable
fun BotaoAdicionais(
    texto: String,
    showAdicionarDialog: Boolean,
    onConfirmar: (String, String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(showAdicionarDialog) }
    Button(
        onClick = {
            showDialog = true
        },
        colors = ButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        )
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Add,
                tint = Color.White,
                contentDescription = "Localized description",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
    if (showDialog) {
        AddAdicionaisDialog(tipo = texto, onDismiss = { showDialog = false }, onConfirmar = onConfirmar)
    }
}

@Composable
fun BotaoPanel(modifier: Modifier = Modifier,
               onClickCancel: () -> Unit = {},
               onClickOk: () -> Unit = {}
) {
        Row (
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = onClickOk,
                modifier = modifier
                    .size(50.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.Green),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Localized description"
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            Button(
                onClick = onClickCancel,
                modifier = modifier.size(50.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Localized description"
                )
            }
        }
}

@Composable
fun PrecosPorText(
    modifier: Modifier = Modifier,
    titulo: String, numero: String,
    preco: String,
    onNumeroChange: (String) -> Unit = {},
    onPrecoChange: (String) -> Unit = {},
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CampoTextoNumerico(
            modifier = modifier
                .weight(0.4f)
                .padding(end = 6.dp),
            titulo = titulo, textoInicial = numero, texto = numero,
            onValueChange = onNumeroChange,
            enabled = enabled
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Localized description",
            modifier = Modifier
                .weight(0.1f)
                .padding(end = 6.dp)
        )
        CampoTextoNumerico(
            modifier = modifier.weight(0.4f),
            titulo = "Preço (R$)", textoInicial = preco, texto = preco,
            onValueChange = onPrecoChange,
            isPreco = true,
            enabled = enabled
        )
    }
}

data class Opcoes(
    var empresa: Boolean = true,
    var data: Boolean = true,
    var local: Boolean = true,
    var entradas: Boolean = true,
    var carnes: Boolean = true,
    var pessoas: Boolean = true,
    var km: Boolean = true,
    var adicionais: Boolean = true,
    var observacao: Boolean = true
)
val OpcoesSaver: Saver<Opcoes, *> = mapSaver(
    save = {
        mapOf(
            "empresa" to it.empresa,
            "data" to it.data,
            "local" to it.local,
            "entradas" to it.entradas,
            "carnes" to it.carnes,
            "pessoas" to it.pessoas,
            "km" to it.km,
            "adicionais" to it.adicionais,
            "observacao" to it.observacao
        )
    },
    restore = {
        Opcoes(
            empresa = it["empresa"] as Boolean,
            data = it["data"] as Boolean,
            local = it["local"] as Boolean,
            entradas = it["entradas"] as Boolean,
            carnes = it["carnes"] as Boolean,
            pessoas = it["pessoas"] as Boolean,
            km = it["km"] as Boolean,
            adicionais = it["adicionais"] as Boolean,
            observacao = it["observacao"] as Boolean
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun AdicionarAlgoDialog(
    modifier: Modifier = Modifier,
    tipo: String, onDismiss: () -> Unit,
    onConfirmar: (nome: String, descricao: String) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var mostrarErro by rememberSaveable { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier.padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Adicionar nova $tipo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                CampoTextoObrigatorio(
                    titulo = "Nome da $tipo",
                    valor = nome,
                    onValueChanged = {
                        nome = it
                        mostrarErro = false
                    },
                    mostrarErro = mostrarErro
                )
                Spacer(modifier = Modifier.height(16.dp))
                CampoTexto(
                    titulo = "Descrição",
                    textoInicial = "Descrição",
                    texto = descricao,
                    onValueChange = { descricao = it }
                )
                Spacer(modifier = Modifier.height(32.dp))
                BotaoPanel(onClickCancel = onDismiss,
                    onClickOk = {
                        if (nome.isNotEmpty()) {
                            onConfirmar(nome, descricao)
                            onDismiss()
                        } else {
                            mostrarErro = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EventoCriadoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier.padding(8.dp),
            shape = MaterialTheme.shapes.large
        )
        {
            IconButton(
            onClick = onDismiss,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Localized description"
            )
        }
                Text(
                    text = "Evento criado com sucesso!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

            }

        }
}

@Composable
fun AddAdicionaisDialog(
    modifier: Modifier = Modifier,
    tipo: String, onDismiss: () -> Unit,
    onConfirmar: (
        nome: String,
        descricao: String, valor: String) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var mostrarErro by rememberSaveable { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier.padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Adicionar nova $tipo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                CampoTextoObrigatorio(
                    titulo = "Nome da $tipo",
                    valor = nome,
                    onValueChanged = {
                        nome = it
                        mostrarErro = false
                    },
                    mostrarErro = mostrarErro
                )
                Spacer(modifier = Modifier.height(16.dp))
                CampoTexto(titulo = "Descrição",
                    textoInicial = "Descrição",
                    texto = descricao,
                    onValueChange = { descricao = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CampoTextoNumerico(
                    titulo = "Valor",
                    textoInicial = "Valor",
                    texto = valor,
                    onValueChange = { valor = it },
                    isPreco = true
                )
                Spacer(modifier = Modifier.height(32.dp))
                BotaoPanel(onClickCancel = onDismiss,
                    onClickOk = {
                        if (nome.isNotEmpty()) {
                            onConfirmar(nome, descricao, valor)
                            onDismiss()
                        } else {
                            mostrarErro = true
                        }
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun TituloPreview() {
    RibsAppTheme {
        Titulo(texto = "Criar Evento")
    }
}

@Preview
@Composable
private fun SwitchPanePreview() {
    RibsAppTheme {
        SwitchPane(texto = "Teste", opt = true, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun CampoTextoPreview() {
    RibsAppTheme {
        CampoTexto(titulo = "Empresa", textoInicial = "Nome")
    }
}

@Preview
@Composable
private fun CardOpcaoPreview() {
    RibsAppTheme {
        CardOpcao(nome = "Entrada", descricao = "Descrição", onCheckedChange = {})
    }
}

@Preview
@Composable
private fun BotaoAdicionarPreview() {
    RibsAppTheme {
        BotaoAdicionar(texto = "Adicionar", showAdicionarDialog = false, onConfirmar = { _, _ ->})
    }
}

@Preview
@Composable
private fun BotaoPanelPreview() {
    RibsAppTheme {
        BotaoPanel()
    }
}

@Preview
@Composable
private fun AdicionarAlgoDialogPreview() {
    RibsAppTheme {
        AdicionarAlgoDialog(tipo = "Entrada", onDismiss = {}, onConfirmar = { _, _ ->})
    }
}

@Preview
@Composable
private fun EventoCriadoDialogPreview() {
    RibsAppTheme {
        EventoCriadoDialog(onDismiss = {})
    }
}

