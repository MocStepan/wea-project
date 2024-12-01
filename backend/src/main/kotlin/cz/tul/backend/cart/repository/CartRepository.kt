package cz.tul.backend.cart.repository

import cz.tul.backend.cart.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository

interface CartRepository : JpaRepository<Cart, Long>
