package io.github.dankosik.investstarterdemo

import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllCandles
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllLastPrices
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllOrderBooks
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllTrades
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllTradingStatuses
import io.github.dankosik.starter.invest.annotation.marketdata.HandleCandle
import io.github.dankosik.starter.invest.annotation.marketdata.HandleLastPrice
import io.github.dankosik.starter.invest.annotation.marketdata.HandleOrderBook
import io.github.dankosik.starter.invest.annotation.marketdata.HandleTrade
import io.github.dankosik.starter.invest.annotation.marketdata.HandleTradingStatus
import io.github.dankosik.starter.invest.annotation.operation.HandleAllPortfolios
import io.github.dankosik.starter.invest.annotation.operation.HandleAllPositions
import io.github.dankosik.starter.invest.annotation.operation.HandlePortfolio
import io.github.dankosik.starter.invest.annotation.operation.HandlePosition
import io.github.dankosik.starter.invest.annotation.order.HandleAllOrders
import io.github.dankosik.starter.invest.annotation.order.HandleOrder
import io.github.dankosik.starter.invest.contract.marketdata.candle.CoroutineCandleHandler
import io.github.dankosik.starter.invest.contract.marketdata.lastprice.CoroutineLastPriceHandler
import io.github.dankosik.starter.invest.contract.marketdata.orderbook.CoroutineOrderBookHandler
import io.github.dankosik.starter.invest.contract.marketdata.status.CoroutineTradingStatusHandler
import io.github.dankosik.starter.invest.contract.marketdata.trade.CoroutineTradeHandler
import io.github.dankosik.starter.invest.contract.operation.portfolio.CoroutinePortfolioHandler
import io.github.dankosik.starter.invest.contract.operation.positions.CoroutinePositionHandler
import io.github.dankosik.starter.invest.contract.orders.CoroutineOrderHandler
import io.github.dankosik.starter.invest.processor.marketdata.CandleStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.marketdata.LastPriceStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.marketdata.OrderBookStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.marketdata.TradeStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.marketdata.TradingStatusStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.marketdata.common.MarketDataStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.operation.PortfolioStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.operation.PositionsStreamProcessorAdapterFactory
import io.github.dankosik.starter.invest.processor.order.OrdersStreamProcessorAdapterFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.piapi.contract.v1.Candle
import ru.tinkoff.piapi.contract.v1.InstrumentType
import ru.tinkoff.piapi.contract.v1.LastPrice
import ru.tinkoff.piapi.contract.v1.OrderBook
import ru.tinkoff.piapi.contract.v1.OrderTrades
import ru.tinkoff.piapi.contract.v1.PortfolioResponse
import ru.tinkoff.piapi.contract.v1.PositionData
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval
import ru.tinkoff.piapi.contract.v1.Trade
import ru.tinkoff.piapi.contract.v1.TradingStatus

@SpringBootApplication
class InvestStarterDemoApplication

fun main(args: Array<String>) {
    runApplication<InvestStarterDemoApplication>(*args)
}

/** Обработка каждого трейда для выбранного тикера/figi/instrumentUid.
 * Использование instrumentType имеет смысл только если вы используете ticker, вместо figi или instrumentUid.
 * Если вы используете ticker при старте приложения будет выполнен запрос на поиск instrumentUid по переданному тикеру.
 * instrumentType нужен лишь для того чтобы сделать это за меньшее количество запросов к api, и с целью уменьшит трату лимитов
 * */
@HandleTrade(ticker = "SiH4", instrumentType = InstrumentType.INSTRUMENT_TYPE_FUTURES)
class DollarTradesHandler : CoroutineTradeHandler {
    override suspend fun handle(trade: Trade) {
        println("DollarTradesHandler $trade")
    }
}

/**
 * Хендлеров может быть сколько угодно, все они будут обрабатываться параллельно.
 * Если указанный тикер был хотя бы в одном из других хендлеров, то instrumentType можно не использовать.
 * Новые запросы для получения тикера не будут исполняться
 * */
@HandleTrade(ticker = "SiH4")
class DollarTradesHandler2 : CoroutineTradeHandler {
    override suspend fun handle(trade: Trade) {
        println("DollarTradesHandler2 $trade")
    }
}

/** обработка всех трейдов (опция beforeEachTradesHandler означает что выполнится этот handler перед всеми остальными) */
@HandleAllTrades(beforeEachTradesHandler = true)
class CommonBeforeEachTradesHandler : CoroutineTradeHandler {

    override suspend fun handle(trade: Trade) {
        println("CommonBeforeEachTradesHandler $trade")
    }
}

/**обработка всех трейдов для указанных тикеров*/
@HandleAllTrades(
    tickers = ["SBER", "LKOH"],
)
class CommonAfterEachTradesHandler : CoroutineTradeHandler {
    override suspend fun handle(trade: Trade) {
        println("CommonAfterEachTradesHandler $trade")
    }
}

/**обработка изменения последней цены для выбранного тикера/figi/instrumentUid */
@HandleLastPrice(ticker = "SiH4")
class DollarLastPriceHandler : CoroutineLastPriceHandler {

    override suspend fun handle(lastPrice: LastPrice) {
        println("DollarLastPriceHandler $lastPrice")
    }
}

/**обработка изменения последней цены всех инструментов (опция beforeEachLastPriceHandler означает что выполнится этот handler перед всеми остальными) */
@HandleAllLastPrices(beforeEachLastPriceHandler = true)
class CommonBeforeEachLastPriceHandler : CoroutineLastPriceHandler {

    override suspend fun handle(lastPrice: LastPrice) {
        println("CommonBeforeEachLastPriceHandler $lastPrice")
    }
}

/**обработка изменения последней цены всех инструментов (опция afterEachLastPriceHandler означает что выполнится этот handler после всех остальных) */
@HandleAllLastPrices(
    figies = ["FUTBR0124000", "BBG004730N88"],
    afterEachLastPriceHandler = true
)
class CommonAfterEachLastPriceHandler : CoroutineLastPriceHandler {

    override suspend fun handle(lastPrice: LastPrice) {
        println("CommonAfterEachLastPriceHandler $lastPrice")
    }
}

/**обработка изменений сткана для выбранного тикера/figi/instrumentUid */
@HandleOrderBook(ticker = "SiH4")
class DollarOrderBookHandler : CoroutineOrderBookHandler {

    override suspend fun handle(orderBook: OrderBook) {
        println("DollarOrderBookHandler $orderBook")
    }
}

/**обработка изменения стакана всех инструментов (опция beforeEachOrderBookHandler означает что выполнится этот handler перед всеми остальными) */
@HandleAllOrderBooks(beforeEachOrderBookHandler = true)
class CommonBeforeEachOrderBookHandler : CoroutineOrderBookHandler {

    override suspend fun handle(orderBook: OrderBook) {
        println("CommonBeforeEachOrderBookHandler $orderBook")
    }
}

/**обработка изменения стакана всех инструментов (опция afterEachOrderBookHandler означает что выполнится этот handler после всех остальных) */
@HandleAllOrderBooks(afterEachOrderBookHandler = true)
class CommonAfterEachOrderBookHandler : CoroutineOrderBookHandler {

    override suspend fun handle(orderBook: OrderBook) {
        println("CommonAfterEachOrderBookHandler $orderBook")
    }
}

/**
обработка свечи для выбранного тикера/figi/instrumentUid и выбранного интервала.
subscriptionInterval нужен чтобы выбрать интервал который будет обрабатывать этот хендлер
 */
@HandleCandle(
    ticker = "SiH4",
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE,
    waitClose = true
)
class DollarCandleHandler : CoroutineCandleHandler {

    override suspend fun handle(candle: Candle) {
        println("DollarCandleHandler $candle")
    }
}

/**
обработка всех свеч выбранного интервала (опция beforeEachCandleHandler означает что выполнится этот handler перед всеми остальными)
subscriptionInterval нужен чтобы выбрать интервал который будет обрабатывать этот хендлер
 */
@HandleAllCandles(
    beforeEachCandleHandler = true,
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE
)
class CommonBeforeEachCandleHandler : CoroutineCandleHandler {

    override suspend fun handle(candle: Candle) {
        println("CommonBeforeEachCandleHandler $candle")
    }
}

/**
обработка всех свеч выбранного интервала
subscriptionInterval нужен чтобы выбрать интервал который будет обрабатывать этот хендлер
 */
@HandleAllCandles(
    tickers = ["CRH4", "BRG4", "SBER", "LKOH"],
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE
)
class CommonAfterEachCandleHandler : CoroutineCandleHandler {

    override suspend fun handle(candle: Candle) {
        println("CommonAfterEachCandleHandler $candle")
    }
}

/**обработка изменений торгового статуса для выбранного тикера/figi/instrumentUid */
@HandleTradingStatus(ticker = "SiH4")
class DollarTradingStatusHandler : CoroutineTradingStatusHandler {

    override suspend fun handle(tradingStatus: TradingStatus) {
        println("DollarTradingStatusHandler $tradingStatus")
    }
}

/**обработка изменений торгового статуса для всех инструментов (опция beforeEachTradingStatusHandler означает что выполнится этот handler перед всеми остальными */
@HandleAllTradingStatuses(beforeEachTradingStatusHandler = true)
class CommonBeforeEachTradingStatusHandler : CoroutineTradingStatusHandler {

    override suspend fun handle(tradingStatus: TradingStatus) {
        println("CommonBeforeEachTradingStatusHandler $tradingStatus")
    }
}

/**обработка изменений торгового статуса для всех инструментов (опция beforeEachTradingStatusHandler означает что выполнится этот handler после всех остальных */
@HandleAllTradingStatuses(afterEachTradingStatusHandler = true)
class CommonAfterEachTradingStatusHandler : CoroutineTradingStatusHandler {

    override suspend fun handle(tradingStatus: TradingStatus) {
        println("CommonAfterEachTradingStatusHandler $tradingStatus")
    }
}

/**обработка изменения позиций портфеля для конкретного аккаунта */
@HandlePortfolio(account = "accountId")
class PortfolioHandler : CoroutinePortfolioHandler {

    override suspend fun handle(portfolioResponse: PortfolioResponse) {
        println("PortfolioHandler $portfolioResponse")
    }
}

/**обработка изменения позиций портфеля для нескольких аккаунтов */
@HandleAllPortfolios(accounts = ["accountId", "accountId2"])
class AllPortfolioHandler : CoroutinePortfolioHandler {

    override suspend fun handle(portfolioResponse: PortfolioResponse) {
        println("AllPortfolioHandler $portfolioResponse")
    }
}

/**обработка изменения позиций для конкретного аккаунта */
@HandlePosition(account = "accountId")
class PositionHandler : CoroutinePositionHandler {

    override suspend fun handle(positionData: PositionData) {
        println("PositionHandler $positionData")
    }
}

/**обработка изменения позиций для нескольких аккаунтов */
@HandleAllPositions(accounts = ["accountId", "accountId2"])
class AllPositionHandler : CoroutinePositionHandler {

    override suspend fun handle(positionData: PositionData) {
        println("AllPositionHandler $positionData")
    }
}

/**обработка ордеров для конкретного аккаунта и конкретного тикера */
@HandleOrder(account = "accountId", ticker = "SiH4")
class OrderHandler : CoroutineOrderHandler {

    override suspend fun handle(orderTrades: OrderTrades) {
        println("OrderHandler $orderTrades")
    }
}

/**обработка всех ордеров из нескольких аккаунтов */
@HandleAllOrders(accounts = ["accountId", "accountId2"])
class AllOrderHandler : CoroutineOrderHandler {

    override suspend fun handle(orderTrades: OrderTrades) {
        println("AllOrderHandler $orderTrades")
    }
}

/** создание обработчиков с помощью @Bean
 * Все runBefore... и runAfter... опциональны
 * */
@Configuration
class Configuration {

    /**Можно обрабатывать все события marketData */
    @Bean
    fun coroutineMarketDataStreamProcessorAdapter() = MarketDataStreamProcessorAdapterFactory
        .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
//        .withFigies(listOf("BBG004730N88")) можно использовать вместо withTickers
//        .withInstrumentUids(listOf("e6123145-9665-43e0-8413-cd61b8aa9b13")) можно использовать вместо withTickers
//        .runAfterEachTradeHandler(true)  опционально
//        .runBeforeEachCandleHandler(true) опционально
//        .runAfterEachLastPriceHandler(true) опционально
//        .runBeforeEachOrderBookHandler(true) опционально
        .createCoroutineHandler { println("CoroutineMarketDataStreamProcessorAdapter: $it") }

    /**Аналог HandleAllLastPrice */
    @Bean
    fun coroutineLastPriceStreamProcessorAdapter() =
        LastPriceStreamProcessorAdapterFactory
//            .runAfterEachLastPriceHandler(true) опционально
//            .runBeforeEachLastPriceHandler(true) опционально
            .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
            .createCoroutineHandler { println("LastPriceStreamProcessorAdapterFactory: $it") }

    /**Аналог HandleAllTrades */
    @Bean
    fun coroutineTradeStreamProcessorAdapter() =
        TradeStreamProcessorAdapterFactory
            .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
            .createCoroutineHandler { println("CoroutineTradeStreamProcessorAdapter $it") }

    /**Аналог HandleAllTradingStatuses */
    @Bean
    fun coroutineTradingStatusStreamProcessorAdapter() =
        TradingStatusStreamProcessorAdapterFactory
            .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
            .createCoroutineHandler { println("coroutineTradingStatusStreamProcessorAdapter $it") }

    /**Аналог HandleAllCandles */
    @Bean
    fun coroutineCandleStreamProcessorAdapter() =
        CandleStreamProcessorAdapterFactory
            .withSubscriptionInterval(SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE)
            .waitClose(true)
            .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
            .createCoroutineHandler { println("CoroutineCandleStreamProcessorAdapter $it") }

    /**Аналог HandleAllOrderBooks */
    @Bean
    fun coroutineOrderBookStreamProcessorAdapter() =
        OrderBookStreamProcessorAdapterFactory
            .withTickers(listOf("CRH4", "BRG4", "SBER", "LKOH"))
            .createCoroutineHandler { println("CoroutineOrderBookStreamProcessorAdapter: $it") }

    /**Аналог HandleAllPortfolios */
    @Bean
    fun coroutinePortfolioStreamProcessorAdapter() =
        PortfolioStreamProcessorAdapterFactory
            .withAccounts(listOf("accountId1", "accountId2"))
            .createCoroutineHandler { println("CoroutinePortfolioStreamProcessorAdapter: $it") }

    /**Аналог HandleAllPositions */
    @Bean
    fun coroutinePositionsStreamProcessorAdapter() =
        PositionsStreamProcessorAdapterFactory
            .withAccounts(listOf("accountId1", "accountId2"))
            .createCoroutineHandler { println("CoroutinePositionsStreamProcessorAdapter: $it") }

    /**Аналог HandleAllOrders */
    @Bean
    fun coroutineOrdersStreamProcessorAdapter() =
        OrdersStreamProcessorAdapterFactory
            .withTickers(listOf("SBER"))
            .withAccounts(listOf("accountId1", "accountId2"))
            .createCoroutineHandler { println("CoroutineOrdersStreamProcessorAdapter: $it") }
}