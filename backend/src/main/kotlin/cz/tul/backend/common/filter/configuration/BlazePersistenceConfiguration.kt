package cz.tul.backend.common.filter.configuration

import com.blazebit.persistence.Criteria
import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.integration.view.spring.EnableEntityViews
import com.blazebit.persistence.view.EntityViewManager
import com.blazebit.persistence.view.spi.EntityViewConfiguration
import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * BlazePersistence configuration for entity views.
 */
@EnableEntityViews(
  basePackages = [
    "cz.tul.backend.book.dto",
    "cz.tul.backend.audit.dto",
    "cz.tul.backend.cart.dto"
  ]
)
@Configuration
class BlazePersistenceConfiguration {

  /**
   * Create criteria builder factory for BlazePersistence.
   */
  @Bean
  fun createCriteriaBuilderFactory(entityManagerFactory: EntityManagerFactory): CriteriaBuilderFactory {
    val config = Criteria.getDefault()
    return config.createCriteriaBuilderFactory(entityManagerFactory)
  }

  /**
   * Create entity view manager for BlazePersistence.
   */
  @Bean
  fun createEntityViewManager(
    cbf: CriteriaBuilderFactory,
    entityViewConfiguration: EntityViewConfiguration
  ): EntityViewManager {
    return entityViewConfiguration.createEntityViewManager(cbf)
  }
}
