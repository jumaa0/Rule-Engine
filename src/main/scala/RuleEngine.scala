package scala

import java.io.{File, PrintWriter}
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import scala.io.Source

/**
 * A rule-based engine for processing orders and calculating discounts based on various criteria.
 */
object RuleEngine extends App {

  // Define the Order class
  case class Order(orderDate: LocalDateTime, productName: String, expiryDate: LocalDate, quantity: Int, unitPrice: Double, channel: String, paymentMethod: String, discount: Double = 0.0)

  /**
   * Loads orders from a CSV file, skipping the header.
   */
  private val ordersForProcessing = Source.fromFile("src/main/resources/TRX1000.csv").getLines().toList.tail

  /**
   * Processes orders from the file, calculates discounts, and stores the result.
   */
  val ordersWithDiscounts = ordersForProcessing.map { orderString =>
    val Array(orderDate, productName, expiryDate, quantityStr, unitPriceStr, channel, paymentMethod) = orderString.split(",")
    val quantity = quantityStr.toInt
    val unitPrice = unitPriceStr.toDouble
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val order = Order(LocalDateTime.parse(orderDate, formatter), productName, LocalDate.parse(expiryDate), quantity, unitPrice, channel, paymentMethod)
    val discountRules = getDiscountRules()
    val discount = calculateDiscount(order, discountRules)
    order.copy(discount = discount)
  }

  /**
   * Outputs the processed orders with discounts to a CSV file.
   */
  val outputFile = new File("src/main/resources/processed_orders.csv")
  val writer = new PrintWriter(outputFile)
  try {
    writer.println("Order Date, Product Name, Expiry Date, Quantity, Unit Price, Channel, Payment Method, Discount")

    ordersWithDiscounts.foreach { order =>
      writer.println(s"${order.orderDate}, ${order.productName}, ${order.expiryDate}, ${order.quantity}, ${order.unitPrice}, ${order.channel}, ${order.paymentMethod}, ${order.discount}")
    }
  } finally {
    writer.close()
  }

  /**
   * Calculates the discount based on the given order and rules.
   *
   * @param order The order for which to calculate the discount.
   * @param rules The list of discount rules to apply.
   * @return The calculated discount as a double.
   */
  def calculateDiscount(order: Order, rules: List[Order => Double]): Double = {
    val discounts = rules.map(rule => rule(order)).sorted.reverse
    if (discounts.length >= 2) {
      val avgDiscount = (discounts(0) + discounts(1)) / 2
      BigDecimal(avgDiscount).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    } else if (discounts.length == 1) {
      BigDecimal(discounts(0)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    } else {
      0.0
    }
  }

  /**
   * Defines a rule that provides a discount based on the quantity of the order.
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def quantityDiscountRule(order: Order): Double = order.quantity match {
    case q if q >= 6 && q <= 9 => 0.05
    case q if q >= 10 && q <= 14 => 0.07
    case q if q > 15 => 0.10
    case _ => 0.0
  }

  /**
   * Defines a rule that provides a discount based on the payment method (e.g., Visa card).
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def visaCardDiscountRule(order: Order): Double = order.paymentMethod.toLowerCase match {
    case "visa" => 0.05
    case _ => 0.0
  }

  /**
   * Defines a rule that provides a discount based on the sales channel (e.g., app).
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def appSalesDiscountRule(order: Order): Double = order.channel.toLowerCase match {
    case "app" if order.quantity >= 1 && order.quantity <= 5 => 0.05
    case "app" if order.quantity >= 6 && order.quantity <= 10 => 0.10
    case "app" if order.quantity >= 11 && order.quantity <= 15 => 0.15
    case _ => 0.0
  }

  /**
   * Defines a rule that provides a discount based on the days remaining until the expiry date.
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def daysRemainingDiscountRule(order: Order): Double = {
    val daysRemaining = LocalDate.now().until(order.expiryDate).getDays
    if (daysRemaining <= 30) {
      0.01 * daysRemaining.toInt
    } else {
      0.0
    }
  }

  /**
   * Defines a rule that provides a discount based on a special date (e.g., 2023-03-23).
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def specialDateDiscountRule(order: Order): Double = order.orderDate.toLocalDate match {
    case date if date.isEqual(LocalDate.parse("2023-03-23")) => 0.5
    case _ => 0.0
  }

  /**
   * Defines a rule that provides a discount based on the product name (e.g., cheese, wine).
   *
   * @param order The order for which to apply the rule.
   * @return The discount as a double.
   */
  def productNameDiscountRule(order: Order): Double = {
    val productName = order.productName.toLowerCase
    if (productName.contains("cheese")) 0.1
    else if (productName.contains("wine")) 0.05
    else 0.0
  }

  /**
   * Gets all discount rules as a list of functions.
   *
   * @return The list of discount rules.
   */
  def getDiscountRules(): List[Order => Double] = List(
    quantityDiscountRule,
    visaCardDiscountRule,
    appSalesDiscountRule,
    daysRemainingDiscountRule,
    specialDateDiscountRule,
    productNameDiscountRule
  )

  /**
   * Counts the number of orders that qualify for discounts.
   */
  val qualifiedOrdersCount = ordersWithDiscounts.count(_.discount > 0)
  println(s"Number of orders qualified for discounts: $qualifiedOrdersCount")
}
