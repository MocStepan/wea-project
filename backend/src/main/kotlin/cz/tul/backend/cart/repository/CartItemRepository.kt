package cz.tul.backend.cart.repository

import cz.tul.backend.cart.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long> {

  fun findByCart_Id(id: Long): List<CartItem>
}
