package com.example.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.BillEntity
import com.example.data.local.entity.BillItemEntity
import com.example.model.Bill
import com.example.model.BillItem

data class BillWithItems(
    @Embedded val bill: BillEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "billId"
    )
    val items: List<BillItemEntity> = emptyList()
) {
    fun toDomain(): Bill {
        return Bill(
            id = bill.id,
            restaurantId = bill.restaurantId,
            billNumber = bill.billNumber,
            timestamp = bill.timestamp,
            dateString = bill.dateString,
            timeString = bill.timeString,
            totalAmount = bill.totalAmount,
            totalItemCount = bill.totalItemCount,
            paymentMethod = bill.paymentMethod,
            notes = bill.notes,
            items = items.map { item ->
                BillItem(
                    id = item.id,
                    billId = item.billId,
                    restaurantId = item.restaurantId,
                    menuItemId = item.menuItemId,
                    foodName = item.foodName,
                    category = item.category,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    totalPrice = item.totalPrice
                )
            }
        )
    }
}
