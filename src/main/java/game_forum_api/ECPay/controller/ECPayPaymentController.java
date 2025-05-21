package game_forum_api.ECPay.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.ECPay.dto.PaymentRequest;
import game_forum_api.ECPay.util.ECPayMacUtil;
import game_forum_api.cashTransaction.service.TransactionsService;
import game_forum_api.order.domain.Orders;
import game_forum_api.order.service.OrdersService;
import game_forum_api.product.service.ProductsService;
import jakarta.servlet.http.HttpServletResponse;

// 測試卡號	4311-9522-2222-2222 / 卡號：4000-2211-1111-1111(可測試交易失敗的情境。)
// 有效年月	輸入大於今日的任意日期 (例：12/30)
// 背面末三碼(CVV)	任意三碼 (例：222)

@RestController
@RequestMapping("/api/payment")
public class ECPayPaymentController {

	private final OrdersService ordersService; // 注入訂單服務
	private final TransactionsService transactionsService;
	private final ProductsService productsService;

	private final String merchantID = "2000132";
	private final String paymentUrl = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

    public ECPayPaymentController(
            OrdersService ordersService, 
            TransactionsService transactionsService, 
            ProductsService productsService) {
        this.ordersService = ordersService;
        this.transactionsService = transactionsService;
        this.productsService = productsService;
    }

	@PostMapping("/info/{orderId}")
	public ResponseEntity<?> createPayment(@PathVariable Integer orderId, @RequestBody PaymentRequest request) {

//		String timestamp = String.valueOf(System.currentTimeMillis()).substring(6, 13);
//
//		String merchantTradeNo = "ORDER" + orderId + timestamp;

	    Orders order = ordersService.getOrderEntityById(orderId);
	    String currentMerchantTradeNo = order.getMerchantTradeNo().replace("-", "").substring(0, 17);
	    ordersService.saveMerchantTradeNo(orderId, currentMerchantTradeNo);

		Map<String, String> params = new HashMap<>();
		params.put("MerchantID", merchantID);
		params.put("MerchantTradeNo", currentMerchantTradeNo);
		params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		params.put("PaymentType", "aio");
		params.put("TotalAmount", String.valueOf(request.getAmount()));
		params.put("TradeDesc", request.getDescription());
		params.put("ItemName", "遊戲商城商品");
		params.put("ChoosePayment", "ALL");
		params.put("EncryptType", "1");

		// 需要 ngrok
		params.put("ReturnURL", "https://natural-integral-imp.ngrok-free.app/api/payment/result");

		params.put("ClientBackURL", "http://localhost:5173/mall/order");

		// 產生 CheckMacValue
		params.put("CheckMacValue", ECPayMacUtil.generateCheckMacValue(params));

		System.out.println("發送給 ECPay 的參數：" + params);

		return ResponseEntity.ok(params);
	}

	@PostMapping("/notify")
	public ResponseEntity<String> paymentNotify(@RequestParam Map<String, String> params) {

		System.out.println("收到 ECPay 付款通知：" + params);

		// 1️先取出 ECPay 回傳的 `CheckMacValue`
		String receivedMac = params.get("CheckMacValue");

		// 2️移除 `CheckMacValue`，因為我們要重新計算
		params.remove("CheckMacValue");

		// 3️重新計算 `CheckMacValue`
		String computedMac = ECPayMacUtil.generateCheckMacValue(params);

		// 4️Debug Log（檢查 `CheckMacValue`）
		System.out.println("ECPay 回傳的 CheckMacValue：" + receivedMac);
		System.out.println("重新計算的 CheckMacValue：" + computedMac);

		// 5️比對是否相等
		if (!computedMac.equalsIgnoreCase(receivedMac)) {
			System.out.println("CheckMacValue 驗證失敗！");
			return ResponseEntity.badRequest().body("0|FAIL");
		}

		// 驗證通過，更新訂單狀態
		String merchantTradeNo = params.get("MerchantTradeNo");
		String tradeStatus = params.get("RtnCode");

		Integer orderId = Integer.parseInt(merchantTradeNo.replaceAll("[^0-9]", ""));
		String status = "1".equals(tradeStatus) ? "shipped" : "canceled";
		ordersService.updateOrderStatus(orderId, status);

		System.out.println("訂單更新成功：" + orderId);
		return ResponseEntity.ok("1|OK"); // ECPay 需要回傳 "1|OK"

	}
	
	@PostMapping("/result")
	public void handleECPayResult(@RequestParam Map<String, String> params, HttpServletResponse response)
	        throws IOException {
	    System.out.println("收到 ECPay 付款結果：" + params);

	    String tradeStatus = params.get("RtnCode"); // 1 = 成功, 其他 = 失敗
	    String merchantTradeNo = params.get("MerchantTradeNo");

	    // 立即檢查回傳資料是否正確
	    if (tradeStatus == null || merchantTradeNo == null) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ECPay 回傳的資料錯誤");
	        return;
	    }

	    Integer orderId = ordersService.getOrderIdByMerchantTradeNo(merchantTradeNo);
	    System.out.println(orderId);
	    if (orderId == null) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "找不到對應的訂單");
	        return;
	    }

	    // 立即回應 ECPay，避免延遲
	    response.setContentType("text/plain");
	    response.getWriter().write("1|OK");
	    response.getWriter().flush();

	    processECPayData(orderId, tradeStatus);

	}

	
	private void processECPayData(Integer orderId, String tradeStatus) {
	    // 更新訂單狀態
	    String status = "1".equals(tradeStatus) ? "pending" : "canceled";
	    ordersService.updateOrderStatus(orderId, status);
	    System.out.println("訂單狀態更新：" + status);

	    // 付款成功時，新增交易記錄 & 扣庫存
	    if ("1".equals(tradeStatus)) {
	    	Orders order = ordersService.getOrderEntityById(orderId);
	    	order.getOrderDetails().size(); // 初始化 lazy loading

    	    transactionsService.createTransactionFromOrder(order, tradeStatus);
    	    productsService.updateStockAfterOrder(order);
	    }
	}


}
