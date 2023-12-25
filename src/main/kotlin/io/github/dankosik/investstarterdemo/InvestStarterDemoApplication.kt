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
import io.github.dankosik.starter.invest.processor.marketdata.CoroutineCandleStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.CoroutineLastPriceStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.CoroutineOrderBookStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.CoroutineTradeStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.CoroutineTradingStatusStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.common.CoroutineMarketDataStreamProcessorAdapter
import io.github.dankosik.starter.invest.processor.marketdata.common.runAfterEachCandleHandler
import io.github.dankosik.starter.invest.processor.marketdata.common.runBeforeEachOrderBookHandler
import io.github.dankosik.starter.invest.processor.marketdata.common.runBeforeEachTradeHandler
import io.github.dankosik.starter.invest.processor.marketdata.runAfterEachCandleBookHandler
import io.github.dankosik.starter.invest.processor.marketdata.runAfterEachLastPriceBookHandler
import io.github.dankosik.starter.invest.processor.marketdata.runAfterEachOrderBookHandler
import io.github.dankosik.starter.invest.processor.marketdata.runAfterEachTradeHandler
import io.github.dankosik.starter.invest.processor.marketdata.runAfterEachTradingStatusHandler
import io.github.dankosik.starter.invest.processor.marketdata.runBeforeEachCandleHandler
import io.github.dankosik.starter.invest.processor.marketdata.runBeforeEachLastPriceHandler
import io.github.dankosik.starter.invest.processor.marketdata.runBeforeEachOrderBookHandler
import io.github.dankosik.starter.invest.processor.marketdata.runBeforeEachTradeHandler
import io.github.dankosik.starter.invest.processor.marketdata.runBeforeEachTradingStatusHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.piapi.contract.v1.Candle
import ru.tinkoff.piapi.contract.v1.InstrumentType
import ru.tinkoff.piapi.contract.v1.LastPrice
import ru.tinkoff.piapi.contract.v1.MarketDataResponse
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

/**обработка всех трейдов (опция afterEachTradesHandler означает что выполнится этот handler после всех остальных) */
@HandleAllTrades(afterEachTradesHandler = true)
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
@HandleAllLastPrices(afterEachLastPriceHandler = true)
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
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE
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
обработка всех свеч выбранного интервала (опция afterEachCandleHandler означает что выполнится этот handler после всех остальных)
subscriptionInterval нужен чтобы выбрать интервал который будет обрабатывать этот хендлер
*/
@HandleAllCandles(
    afterEachCandleHandler = true,
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
    fun coroutineMarketDataStreamProcessorAdapter(): CoroutineMarketDataStreamProcessorAdapter =
        CoroutineMarketDataStreamProcessorAdapter { marketDataResponse: MarketDataResponse ->
            println("CoroutineMarketDataStreamProcessorAdapter $marketDataResponse")
        }.runBeforeEachTradeHandler()
            .runBeforeEachOrderBookHandler()
            .runAfterEachCandleHandler()

    /**Аналог HandleAllLastPrice */
    @Bean
    fun coroutineLastPriceStreamProcessorAdapter(): CoroutineLastPriceStreamProcessorAdapter =
        CoroutineLastPriceStreamProcessorAdapter { lastPrice: LastPrice ->
            println("coroutineLastPriceStreamProcessorAdapter $lastPrice")
        }.runBeforeEachLastPriceHandler()
            .runAfterEachLastPriceBookHandler()

    /**Аналог HandleAllTrades */
    @Bean
    fun coroutineTradeStreamProcessorAdapter(): CoroutineTradeStreamProcessorAdapter =
        CoroutineTradeStreamProcessorAdapter { trade: Trade ->
            println("CoroutineTradeStreamProcessorAdapter $trade")
        }.runBeforeEachTradeHandler()
            .runAfterEachTradeHandler()

    /**Аналог HandleAllTradingStatuses */
    @Bean
    fun coroutineTradingStatusStreamProcessorAdapter(): CoroutineTradingStatusStreamProcessorAdapter =
        CoroutineTradingStatusStreamProcessorAdapter { tradingStatus: TradingStatus ->
            println("CoroutineTradingStatusStreamProcessorAdapter $tradingStatus")
        }.runBeforeEachTradingStatusHandler()
            .runAfterEachTradingStatusHandler()

    /**Аналог HandleAllCandles */
    @Bean
    fun coroutineCandleStreamProcessorAdapter(): CoroutineCandleStreamProcessorAdapter =
        CoroutineCandleStreamProcessorAdapter { candle: Candle ->
            println("CoroutineCandleStreamProcessorAdapter $candle")
        }.runBeforeEachCandleHandler()
            .runAfterEachCandleBookHandler()

    /**Аналог HandleAllOrderBooks */
    @Bean
    fun coroutineOrderBookStreamProcessorAdapter(): CoroutineOrderBookStreamProcessorAdapter =
        CoroutineOrderBookStreamProcessorAdapter { orderBook: OrderBook ->
            println("CoroutineOrderBookStreamProcessorAdapter $orderBook")
        }.runAfterEachOrderBookHandler()
            .runBeforeEachOrderBookHandler()
}