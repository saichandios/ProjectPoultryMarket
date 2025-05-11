package com.gsggroups.poultrymarket.Utils

import com.gsggroups.poultrymarket.Common.RetrofitClient

object ApiConstants {
     val baseUrl = RetrofitClient.BASE_URL
     val register = baseUrl+"/registerUser"
}