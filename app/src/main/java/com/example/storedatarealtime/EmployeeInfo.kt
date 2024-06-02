package com.example.storedatarealtime

class EmployeeInfo {
    var employeeName: String? = null
    var employeeContactNumber: String? = null
    var employeeAddress: String? = null
    var imageData: String? = null

    constructor(employeeName: String?, employeeContactNumber: String?, employeeAddress: String?, imageData: String?) {
        this.employeeName = employeeName
        this.employeeContactNumber = employeeContactNumber
        this.employeeAddress = employeeAddress
        this.imageData = imageData
    }
}