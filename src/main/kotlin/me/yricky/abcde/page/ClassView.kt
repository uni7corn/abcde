package me.yricky.abcde.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yricky.abcde.AppState
import me.yricky.abcde.ui.*
import me.yricky.oh.abcd.cfm.AbcField
import me.yricky.oh.abcd.cfm.AbcMethod
import me.yricky.oh.abcd.cfm.AbcClass
import me.yricky.oh.abcd.cfm.isModuleRecordIdx


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassViewPage(
    modifier: Modifier,
    appState: AppState,
    clazz: AbcClass
) {
    VerticalTabAndContent(modifier, listOf(
        composeSelectContent{ _:Boolean ->
            Image(clazz.icon(), null, Modifier.fillMaxSize(), colorFilter = grayColorFilter)
        } to composeContent{
            Column(Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(clazz.icon(), null, modifier = Modifier.padding(8.dp).size(24.dp))
                    Text(clazz.name, style = MaterialTheme.typography.titleLarge)
                }
                var fieldFilter by remember {
                    mutableStateOf("")
                }
                val filteredFields: List<AbcField> = remember(fieldFilter) {
                    clazz.fields.filter { it.name.contains(fieldFilter) }
                }
                var methodFilter by remember {
                    mutableStateOf("")
                }
                val filteredMethods: List<AbcMethod> = remember(methodFilter) {
                    clazz.methods.filter { it.name.contains(methodFilter) }
                }
                val focus = LocalFocusManager.current
                LazyColumnWithScrollBar {
                    stickyHeader {
                        Surface(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${clazz.numFields}个字段")
                                Image(Icons.search(), null)
                                BasicTextField(
                                    value = fieldFilter,
                                    onValueChange = { fieldFilter = it.replace(" ", "").replace("\n", "") },
                                    textStyle = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.onSecondaryContainer),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }
                        }
                    }
                    items(filteredFields) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clearFocusWhenEnter(focus).fillMaxWidth()
                        ) {
                            Image(it.icon(), null)
                            if (it.accessFlags.isEnum) {
                                Image(Icons.enum(), null)
                            }
                            if (it.isModuleRecordIdx()) {
                                Image(Icons.pkg(), null, modifier = Modifier.clickable {

                                })
                            }
                            SelectionContainer {
                                Text(
                                    it.defineStr(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 0.sp
                                )
                            }
                        }
                    }
                    stickyHeader {
                        Surface(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${clazz.numMethods}个方法")
                                Image(Icons.search(), null)
                                BasicTextField(
                                    value = methodFilter,
                                    onValueChange = { methodFilter = it.replace(" ", "").replace("\n", "") },
                                    textStyle = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.onSecondaryContainer),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }
                        }
                    }
                    items(filteredMethods) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clearFocusWhenEnter(focus).fillMaxWidth().clickable { appState.openCode(it) }
                        ) {
                            Image(it.icon(), null)
                            it.codeItem?.let { c ->
                                Image(Icons.watch(), null)
                            }
                            Text(
                                it.defineStr(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 0.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }, composeSelectContent{ _:Boolean ->
            Image(Icons.pkg(), null, Modifier.fillMaxSize().alpha(0.5f), colorFilter = grayColorFilter)
        } to composeContent{
            Column(Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(clazz.icon(), null, modifier = Modifier.padding(8.dp).size(24.dp))
                    Text(clazz.name, style = MaterialTheme.typography.titleLarge)
                }
                LazyColumnWithScrollBar {
                    clazz.moduleInfo?.let { m ->
                        stickyHeader {
                            Text("ModuleRequests(${m.moduleRequestNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.moduleRequests){
                            Text("- $it")
                        }
                        stickyHeader {
                            Text("RegularImports(${m.regularImportNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.regularImports){
                            Column(Modifier.padding(4.dp)) {
                                Text("- localName:${it.localName}")
                                Text("- importName:${it.importName}")
                                Text("- moduleRequest:${it.moduleRequest}")
                            }
                        }
                        stickyHeader {
                            Text("NamespaceImports(${m.namespaceImportNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.namespaceImports){
                            Column(Modifier.padding(4.dp)) {
                                Text("- localName:${it.localName}")
                                Text("- moduleRequest:${it.moduleRequest}")
                            }
                        }
                        stickyHeader {
                            Text("LocalExports(${m.localExportNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.localExports){
                            Column(Modifier.padding(4.dp)) {
                                Text("- localName:${it.localName}")
                                Text("- exportName:${it.exportName}")
                            }
                        }
                        stickyHeader {
                            Text("IndirectExports(${m.indirectExportNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.indirectExports){
                            Column(Modifier.padding(4.dp)) {
                                Text("- importName:${it.importName}")
                                Text("- exportName:${it.exportName}")
                                Text("- moduleRequest:${it.moduleRequest}")
                            }
                        }
                        stickyHeader {
                            Text("StarExports(${m.starExportNum})",Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface))
                        }
                        items(m.starExports){
                            Column {
                                Text("- moduleRequest:${it.moduleRequest}")
                            }
                        }
                    }
                }
            }
        }
    ))
}
