# Rules Engine using Scala

The Rules Engine is a Scala application designed to process orders, apply discount rules, and calculate final prices based on specific criteria. It reads order data from a CSV file, applies discount rules, and then calculates discounts and total prices for each order.

## Features

- **Discount Rules**: Implements various discount rules based on product types, remaining days before expiry, quantity sold, and special dates.
- **Logging Mechanism**: Logs engine rule interactions and errors to a text file for debugging and auditing purposes.

## Discount Rules Implemented

### More Than 5 Qualifier Rule

Checks if the quantity of a product in an order is more than 5.

**Discount calculation:**
- 5% discount for quantities 6-9 units.
- 7% discount for quantities 10-14 units.
- 10% discount for quantities more than 15 units.

### Cheese and Wine Qualifier Rule

Identifies orders containing wine or cheese products.

**Discount calculation:**
- 5% discount for wine products.
- 10% discount for cheese products.

### Less Than 30 Days to Expiry Qualifier Rule

Checks if there are less than 30 days remaining for the product to expire.

**Discount calculation:**
- Gradual discount based on days remaining, starting from 1% and increasing by 1% per day, up to a maximum of 30%.

### Products Sold on 23rd of March Qualifier Rule

Identifies orders made on the 23rd of March.

**Discount calculation:**
- 50% discount for orders made on this date.

### App Usage Qualifier Rule

Checks if the sale was made through the App.

**Discount calculation:**
- 5% discount for quantities 1-5 units.
- 10% discount for quantities 6-10 units.
- 15% discount for quantities more than 10 units.

### Visa Card Usage Qualifier Rule

Identifies orders made using Visa cards.

**Discount calculation:**
- 5% discount.

### Clone Repository


- `https://github.com/jumaa0/Rule-Engine.git`

### Change directory
- `cd ./src/main/scala`

### Contributing

Fork the project
Create your feature branch (`git checkout -b feature`)
Commit your changes (`git commit -am 'Add new feature'`)
Push to the branch (`git push origin feature`)
Create a new Pull Request
