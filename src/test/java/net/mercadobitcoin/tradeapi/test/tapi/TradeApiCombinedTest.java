/**
 * under the MIT License (MIT)
 * Copyright (c) 2015 Mercado Bitcoin Servicos Digitais Ltda.
 * @see more details in /LICENSE.txt
 */

package net.mercadobitcoin.tradeapi.test.tapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.mercadobitcoin.common.exception.MercadoBitcoinException;
import net.mercadobitcoin.tradeapi.service.ApiService;
import net.mercadobitcoin.tradeapi.service.TradeApiService;
import net.mercadobitcoin.tradeapi.test.base.AbstractBaseApiTest;
import net.mercadobitcoin.tradeapi.test.base.UserInfo;
import net.mercadobitcoin.tradeapi.to.AccountBalance;
import net.mercadobitcoin.tradeapi.to.Operation;
import net.mercadobitcoin.tradeapi.to.Order;
import net.mercadobitcoin.tradeapi.to.Order.CoinPair;
import net.mercadobitcoin.tradeapi.to.Order.OrderStatus;
import net.mercadobitcoin.tradeapi.to.OrderFilter;
import net.mercadobitcoin.tradeapi.to.Orderbook;

import org.junit.BeforeClass;
import org.junit.Test;

public class TradeApiCombinedTest extends AbstractBaseApiTest {

	private static ApiService api;
	private static TradeApiService tapi;
	
	@BeforeClass
	public static void init() throws MercadoBitcoinException {
		api = new ApiService();
		tapi = new TradeApiService(UserInfo.MY_TAPI_CODE, UserInfo.MY_TAPI_KEY);
	}

	@Test
	public void testGetAndExecuteBuyOrder() throws MercadoBitcoinException {
		Orderbook aux = api.orderbook(CoinPair.BTC_BRL);
		Order firstSellOrder = aux.getAsks()[0];
		assertNotNull(firstSellOrder);
		
		Order buyOrder = tapi.createBuyOrder(firstSellOrder.getPair(),
						Order.MINIMUM_VOLUME, firstSellOrder.getPrice());
		
		assertNotNull(buyOrder);
		
		List<Operation> operations = buyOrder.getOperations();
		assertNotNull(operations);
	}
	
	@Test
	public void testGetAndExecuteSellOrder() throws MercadoBitcoinException {
		Orderbook aux = api.orderbook(CoinPair.BTC_BRL);
		Order firstBuyOrder = aux.getBids()[0];
		assertNotNull(firstBuyOrder);
			
		Order sellOrder = tapi.createSellOrder(firstBuyOrder.getPair(),
						Order.MINIMUM_VOLUME, firstBuyOrder.getPrice());
		
		List<Operation> operations = sellOrder.getOperations();
		assertNotNull(operations);	
	}
	
	@Test
	public void testClearAllOrders() throws MercadoBitcoinException {
		tapi.getAccountInfo();
		
		OrderFilter filter = new OrderFilter(CoinPair.BTC_BRL);
		filter.setStatus(OrderStatus.ACTIVE);
		
		List<Order> orderList = tapi.listOrders(filter);
		assertNotNull(orderList);
		while (!orderList.isEmpty()) {
			Order order = orderList.remove(0);
			Order cancelOrder = tapi.cancelOrder(order);
			assertNotNull(cancelOrder);
			assertEquals(cancelOrder.getStatus(),  OrderStatus.CANCELED.getValue());
		}
		filter = new OrderFilter(CoinPair.LTC_BRL);
		filter.setStatus(OrderStatus.ACTIVE);
		
		orderList = tapi.listOrders(filter);
		assertNotNull(orderList);
		while (!orderList.isEmpty()) {
			Order order = orderList.remove(0);
			Order cancelOrder = tapi.cancelOrder(order);
			assertNotNull(cancelOrder);
			assertEquals(cancelOrder.getStatus(),  OrderStatus.CANCELED.getValue());
		}
		AccountBalance accountBalance = tapi.getAccountInfo();
		assertNotNull(accountBalance);
	}
	
	@Test
	public void testCreateFindAndCancelOrder() throws MercadoBitcoinException, InterruptedException {
		Order buyOrder = tapi.createBuyOrder(CoinPair.BTC_BRL, Order.MINIMUM_VOLUME, LOW_PRICE);
		assertNotNull(buyOrder);
		
		OrderFilter filter = new OrderFilter(CoinPair.BTC_BRL);
		filter.setStatus(OrderStatus.ACTIVE);

		List<Order> orderList = tapi.listOrders(filter);
		assertNotNull(orderList);
		assertTrue(!orderList.isEmpty());
	}
	
}
