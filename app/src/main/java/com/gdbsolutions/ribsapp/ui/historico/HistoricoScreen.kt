package com.gdbsolutions.ribsapp.ui.historico

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gdbsolutions.ribsapp.R
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import com.gdbsolutions.ribsapp.ui.criar.BotaoPanel
import com.gdbsolutions.ribsapp.ui.criar.CriarEventoViewModel
import com.gdbsolutions.ribsapp.ui.criar.Titulo
import com.gdbsolutions.ribsapp.ui.theme.LightGrey
import com.gdbsolutions.ribsapp.ui.theme.RibsAppTheme
import com.gdbsolutions.ribsapp.utils.converters.toFormattedDateTimeString
import com.gdbsolutions.ribsapp.utils.converters.toStringBR
import com.gdbsolutions.ribsapp.utils.html.shareHtmlAsPdf
import com.gdbsolutions.ribsapp.utils.html.toHtmlOrcamento
import com.gdbsolutions.ribsapp.utils.webview.rememberWebViewWithHtml

@Composable
fun HistoricoScreen(modifier: Modifier = Modifier, viewModel: CriarEventoViewModel) {
    val eventos = viewModel.eventosCompleto.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.carregarEventosCompleto()
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item{
            Spacer(modifier = Modifier.height(12.dp))
            Titulo(texto = "Histórico de Eventos")
            Spacer(modifier = Modifier.height(12.dp))
        }
        listaDeEventos(eventos = eventos.value, viewModel = viewModel)
    }
}

fun LazyListScope.listaDeEventos(
    eventos: List<EventoCompleto>,
    viewModel: CriarEventoViewModel
) {
    items(eventos.size) { index ->
        CardEvento(
            nome = eventos[index].evento.nomeEmpresa?:"N/A",
            dataEvento = eventos[index].evento.dataEvento?:"Não Informado",
            dataCriacao = eventos[index].evento.dataCriacao.toFormattedDateTimeString(),
            localEvento = eventos[index].evento.localEvento?:"Não Informado",
            numPessoas = eventos[index].evento.numPessoas.toString(),
            observacoes = eventos[index].evento.observacoes?: "",
            total = eventos[index].precoTotal.toStringBR(),
            carnes = eventos[index].carnes.map { it.nome },
            entradas = eventos[index].entradas.map { it.nome },
            adicionais = eventos[index].adicionais.map { it.nome },
            eventoCompleto = eventos[index],
            viewModel = viewModel
        )
    }
}

@Composable
fun CardEvento(
    modifier: Modifier = Modifier,
    nome: String,
    dataEvento: String,
    dataCriacao: String,
    localEvento: String,
    numPessoas: String,
    observacoes: String,
    carnes: List<String> = emptyList(),
    entradas: List<String> = emptyList(),
    adicionais: List<String> = emptyList(),
    eventoCompleto: EventoCompleto,
    total: String = "R$ 0,00",
    viewModel: CriarEventoViewModel = viewModel()
) {
    var expended by remember { mutableStateOf(false )}
    var showListaCarnes by remember { mutableStateOf(false) }
    var showListaEntradas by remember { mutableStateOf(false) }
    var showListaAdicionais by remember { mutableStateOf(false) }
    var showOrcamentoDialog by remember { mutableStateOf(false) }
    var showDeletarDialog by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = { expended = !expended }),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evento $nome - $dataEvento",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = modifier.padding(8.dp)
                )
                }
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data de Criação: $dataCriacao",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier.padding(16.dp)
                )
                if (expended) {
                    IconButton(
                        onClick = { showDeletarDialog = true },
                        modifier = modifier.padding(16.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = expended,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Text(
                        text = "Informações do Evento",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                    Linha()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Empresa:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = nome,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dia do Evento:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = dataEvento,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Local do Evento:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = localEvento,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(
                            text = "Número de Pessoas:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = numPessoas,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Carnes:",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                                textAlign = TextAlign.Start
                            )
                            Button(
                                onClick = { showListaCarnes = true },
                                shape = MaterialTheme.shapes.small,
                                modifier = modifier.padding(start = 6.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Ver Carnes"
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Entradas:",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                                textAlign = TextAlign.Start
                            )
                            Button(
                                onClick = {  showListaEntradas = true },
                                shape = MaterialTheme.shapes.small,
                                modifier = modifier.padding(end = 6.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Ver Entradas"
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Adicionais",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = modifier.padding(bottom = 4.dp, start = 16.dp),
                                textAlign = TextAlign.Start
                            )
                            Button(
                                onClick = { showListaAdicionais = true },
                                shape = MaterialTheme.shapes.small,
                                modifier = modifier.padding(start = 6.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Ver Adicionais"
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Observações:",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = observacoes,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = modifier.padding(bottom = 4.dp, end = 16.dp),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    Text(
                        text = "Total: R$ $total",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = modifier.padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    )
                    ButtonBar(onClickOrcamento = { showOrcamentoDialog = true })
                    Spacer(modifier = modifier.height(16.dp))
                }
                }

        }
        if (showListaCarnes) {
            ListDialog(
                onDismiss = { showListaCarnes = false },
                titulo = "Carnes",
                lista = carnes
            )
        }
        if (showListaEntradas) {
            ListDialog(
                onDismiss = { showListaEntradas = false },
                titulo = "Entradas",
                lista = entradas
            )
        }
        if (showListaAdicionais) {
            ListDialog(
                onDismiss = { showListaAdicionais = false },
                titulo = "Adicionais",
                lista = adicionais
            )
        }
        if (showOrcamentoDialog) {
            OrcamentoDialog(
                eventoCompleto = eventoCompleto,
                onDismiss = { showOrcamentoDialog = false }
            )
        }
        if (showDeletarDialog) {
            DeletarEventoDialog(
                onDismiss = { showDeletarDialog = false },
                onConfirm = {
                    viewModel.deleteEvento(eventoCompleto.evento)
                    showDeletarDialog = false
                    viewModel.carregarEventosCompleto()
                }
            )
        }
    }
}


@Composable
fun ButtonBar(
    modifier: Modifier = Modifier,
    onClickOrcamento: () -> Unit = {},
    onClickCardapio: () -> Unit = {},
    onClickRecibo: () -> Unit = {}
) {
    val iconCardapio = painterResource(id = R.drawable.cardapio_icon)
    val iconOrcamento = painterResource(id = R.drawable.orcamento_icon)
    val iconRecibo = painterResource(id = R.drawable.recibo_icon)
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Button(
            onClick = onClickOrcamento,
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .padding(end = 2.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = iconOrcamento,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = modifier.padding(end = 6.dp)
                )
                Text(
                    text = "Orçamento"
                )
            }
        }
        Button(
            onClick = onClickCardapio,
            modifier = modifier
                .padding(end = 2.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = iconCardapio,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = modifier.padding(end = 6.dp)
                )
                Text(
                    text = "Cardápio"
                )
            }
        }
        Button(
            onClick = onClickRecibo,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = iconRecibo,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = modifier.padding(end = 6.dp)
                )
                Text(
                    text = "Recibo"
                )
            }
        }
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
fun DeletarEventoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tem certeza que deseja deletar este evento?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier.padding(bottom = 16.dp)
                )
                BotaoPanel(
                    onClickOk = onConfirm,
                    onClickCancel = onDismiss,
                )
            }
        }
    }
}

@Composable
fun ListDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    titulo: String,
    lista: List<String>
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            LazyColumn(
                modifier = modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                item {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = modifier.height(6.dp))
                }
                items(lista.size) { index ->
                    Text(
                        text = lista[index],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier.padding(6.dp)
                    )
                }
                item {
                    Spacer(modifier = modifier.height(16.dp))
                    IconButton(
                            onClick = onDismiss,
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Red)
                    ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                }
            }

        }
    }
}

@Composable
fun OrcamentoDialog(
    eventoCompleto: EventoCompleto,
    onDismiss: () -> Unit
) {
    val htmlContent = remember(eventoCompleto) {
        eventoCompleto.toHtmlOrcamento()
    }
    val context = LocalContext.current
    val webView = rememberWebViewWithHtml(htmlContent)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visualizar Orçamento",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }
                AndroidView(
                    factory = { webView },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                Button(
                    onClick = {
                        // Agora passamos a webView existente para a função
                        shareHtmlAsPdf(context, htmlContent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Compartilhar PDF")
                }
            }
        }
    }
}

@Preview
@Composable
private fun ButtonBarPreview() {
    RibsAppTheme {
        ButtonBar()
    }

}

//@Preview
//@Composable
//private fun CardEventoPreview() {
//    RibsAppTheme {
//        CardEvento(nome = "Evento 1", dataEvento = "01/01/23", dataCriacao = "01/01/2023", localEvento = "Local", numPessoas = "10", observacoes = "Observações")
//    }
//}