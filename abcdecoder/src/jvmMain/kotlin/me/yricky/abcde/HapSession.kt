package me.yricky.abcde

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import me.yricky.abcde.page.*
import me.yricky.oh.abcd.cfm.AbcClass
import me.yricky.oh.abcd.cfm.AbcMethod
import me.yricky.oh.resde.ResIndexBuf

class HapSession(
    val hapView: HapView?
) {
    val pageStack = mutableStateListOf<AttachHapPage>()

    var currPage: Page? by mutableStateOf(hapView)
        private set

    private fun navPage(page: Page?){
        if(page == null && hapView != null){
            println("nav to null at ${hapView.name}")
            return
        }
        currPage = page
        println("route to ${page?.navString}")
    }

    fun goDefault(){
        navPage(hapView)
    }

    fun openPage(page: AttachHapPage){
        navPage(page)
        if(!pageStack.contains(page)){
            pageStack.add(page)
        }
    }

    fun openClass(classItem: AbcClass){
        ClassView(classItem,this).also {
            navPage(it)
            if(!pageStack.contains(it)){
                pageStack.add(it)
            }
        }
    }

    fun openCode(method: AbcMethod){
        method.codeItem?.let {
            CodeView(it,this).also {
                navPage(it)
                if(!pageStack.contains(it)){
                    pageStack.add(it)
                }
            }
        }
    }

    fun closePage(page: AttachHapPage){
        val index = pageStack.indexOf(page)
        if(index >= 0){
            pageStack.removeAt(index)
            if(currPage == page){
                navPage(pageStack.getOrNull(index) ?: pageStack.lastOrNull() ?: hapView)
            }
        }
    }

    fun gotoPage(page: Page){
        if(page is AttachHapPage){
            if(!pageStack.contains(page)){
                pageStack.add(page)
            }
            navPage(page)
        } else if(page == hapView){
            navPage(page)
        }
    }

    suspend fun openedRes(): ResIndexBuf?{
        return hapView?.getDefaultResourceIndex()
    }

    @Composable
    fun loadPainterInZip(entryName:String):Painter? = hapView?.loadPainterInZip(entryName)
    val hapConfig = hapView?.config
}