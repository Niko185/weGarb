package com.example.wegarb.data.estimate

import com.example.wegarb.data.arrays.ArraysGarb
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.presentation.vm.MainViewModel

class SearchEstimate() {
    private val mainViewModel = MainViewModel()
    private val arraysGarb = ArraysGarb()

     fun estimate() {
         val res = mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature
         if (res in -60..-35) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
         } else if (res in -34..-27) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
         } else if (res in -26..-15) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
         } else if (res in -14..-5) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
         } else if (res in -4..8) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
         } else if (res in 9..14) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
         } else if (res in 15..18) {
             mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
         } else if (res in 19..24) {
             mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
         } else if (res in 25..30) {
             mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
         } else if (res in 31..55) {
             mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
         } else {
             mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
         }

     }
}