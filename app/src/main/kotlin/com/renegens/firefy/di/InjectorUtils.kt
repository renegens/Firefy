package com.renegens.firefy.di

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import com.renegens.firefy.MyApplication

fun Context.getDagger() = (applicationContext as MyApplication).getComponent()

fun Activity.getDagger() = (application as MyApplication).getComponent()

fun Fragment.getDagger() = (this.context.applicationContext as MyApplication).getComponent()

fun View.getDagger() = (this.context.applicationContext as MyApplication).getComponent()
