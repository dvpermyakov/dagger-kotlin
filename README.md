# Dependency injector library for Kotlin
[![CircleCI](https://circleci.com/gh/dvpermyakov/dagger-kotlin/tree/master.svg?style=shield)](https://circleci.com/gh/dvpermyakov/dagger-kotlin/tree/master)
[![Bintray](https://api.bintray.com/packages/dvpermyakov/maven/com.dvpermyakov.dagger-kotlin/images/download.svg)](https://bintray.com/dvpermyakov/maven/com.dvpermyakov.dagger-kotlin/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This library is very similar to [dagger-2](https://github.com/google/dagger) library, but it generates kotlin files `(*.kt)` instead of java files `(*.java)`. It uses `apt` to generate components and factories. 

You can create a component interface with `modules` and `dependencies` fileds (`@Component.Factory` is optional):
```Kotlin
@Component(
    modules = [MainModule::class, DatabaseModule::class, RepositoryModule::class],
    dependencies = [DataDependencies::class]
)
interface MainComponent : ProfileDependencies, GlobalConfigDependencies {

    fun getSubcomponent(): MainSubcomponent

    fun getTransactionViewModel(): TransactionViewModel

    fun getSampleConfig(): GlobalConfig

    fun inject(view: TransactionView)

    @Component.Factory
    interface Factory {
        fun createNewInstance(
            @BindsInstance nConfig: NetworkConfig,
            dataDependencies: DataDependencies,
            @BindsInstance dConfig: DatabaseConfig,
            mainModule: MainModule
        ): MainComponent
    }
}
```

You can use a module with `@Provide` annotated methods:
```Kotlin
@Module
class DatabaseModule {

    @Singleton
    @Provide
    fun getDatabase(): Database {
        return Database()
    }

}
```

Yuu can use a module with `@Binds` annotated methods: 
```Kotlin
@Module
interface RepositoryModule {

    @Binds
    fun provideSampleRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

}
```

You can create a subcomponent interface with a `modules` filed:
```Kotlin
@Subcomponent(modules = [ParameterModule::class])
interface MainSubcomponent : ProfileDependencies {
    fun getTransactionViewModel(): TransactionViewModel

    fun getParameterConfig(): ParameterConfig
}
```

You can use `@Inject` in constructors:
```Kotlin
class TransactionRepositoryImpl @Inject constructor(
    private val config: GlobalConfig,
    private val profile: ProfileModel
) : TransactionRepository {

    override fun getTransactions(card: CardModel): List<TransactionModel> {
        return if (config.transactionAvailable && profile.id.isNotBlank()) {
            when (card.id) {
                "1" -> {
                    listOf(
                        TransactionModel("1"),
                        TransactionModel("2")
                    )
                }
                "2" -> {
                    listOf(
                        TransactionModel("3"),
                        TransactionModel("4")
                    )
                }
                else -> emptyList()
            }
        } else emptyList()
    }

}
```

You can use `@Inject` in fileds:
```Kotlin
class TransactionView {

    @Inject
    lateinit var transactionViewModel: TransactionViewModel

    @Inject
    lateinit var accountViewModel: AccountViewModel

}
```

Gradle: 
```Groovy
repositories {
   maven {
        url  "https://dl.bintray.com/dvpermyakov/maven" 
    }
}
dependencies {
   implementation 'com.dvpermyakov:dagger-kotlin:0.0.2'
}
```
