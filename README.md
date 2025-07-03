# Sporty Shoes E-commerce Website

A complete e-commerce website for sports shoes built with Spring Boot, MySQL, and Thymeleaf.

## Features

### Customer Features

- User registration and login
- Browse products by category
- Search products
- View product details
- Add products to cart
- Purchase products (Buy Now or Cart Checkout)
- View order history and order details
- User profile management
- Responsive design

### Admin Features

- Admin login with secure authentication
- Dashboard with statistics
- User management (view and search users)
- Product management (add, view products)
- Category management (add, view categories)
- **Data Seeding**: One-click dummy data generation for testing
- **Data Management**: Clear all products functionality
- Purchase reports with date and category filters
- Change admin password

## Technology Stack

- **Backend**: Spring Boot 3.4.7
- **Database**: MySQL
- **Frontend**: Thymeleaf, HTML5, CSS3
- **Security**: Spring Security with BCrypt password encoding
- **Build Tool**: Maven
- **Java Version**: 21

## Database Setup

1. Install MySQL and create a database named `sportyshoe_db`
2. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sportyshoe_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8080`

## Default Admin Credentials

- **Username**: admin
- **Password**: admin123

## Quick Setup with Sample Data

### 1. Automatic Data Seeding (Recommended)

After starting the application:

1. Login to admin panel: `http://localhost:8080/admin/login`
2. Go to **Products** section
3. Click **"Seed Dummy Data"** button
4. Instantly get 12 sample products across 5 categories

### 2. Manual SQL Setup

Run the provided SQL script:

```bash
mysql -u root -p sportyshoe < sample-data-insert.sql
```

📖 **Detailed Guide**: See `DATA_SEEDING_GUIDE.md` for complete instructions

## Project Structure

```
src/main/java/com/sporty_shoe/
├── bean/          # Entity classes (Admin, User, Product, Category, Purchase)
├── repository/    # JPA repositories
├── service/       # Business logic services
├── controller/    # Web controllers
└── config/        # Configuration classes (Security, Data Initialization)

src/main/resources/
├── templates/     # Thymeleaf HTML templates
├── static/        # Static resources (CSS, JS, images)
└── application.properties  # Application configuration
```

## Key Features Implemented

### 1. Admin Management

- Secure admin login
- Password change functionality
- Dashboard with key metrics

### 2. User Management

- User registration with validation
- User search functionality
- User profile management

### 3. Product Management

- Add new products with categories
- Product listing with filters
- Product search functionality
- Stock management

### 4. Category Management

- Add new categories
- View all categories
- Category-based product filtering

### 5. Purchase Reports

- View all purchases
- Filter by date range
- Filter by category
- Revenue calculations

## Security Features

- BCrypt password encryption
- Session-based authentication
- Input validation
- SQL injection prevention through JPA

## Future Enhancements

- Shopping cart functionality
- Order management system
- Payment integration
- Image upload for products
- Email notifications
- Advanced reporting
- Product reviews and ratings

## API Endpoints

### Public Endpoints

- `GET /` - Home page
- `GET /products` - Product listing
- `GET /products/{id}` - Product details
- `GET /login` - User login page
- `GET /register` - User registration page
- `POST /user/register` - User registration
- `POST /user/login` - User login

### Admin Endpoints

- `GET /admin/login` - Admin login page
- `POST /admin/login` - Admin login
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - User management
- `GET /admin/products` - Product management
- `GET /admin/categories` - Category management
- `GET /admin/reports` - Purchase reports
- `GET /admin/change-password` - Change password page

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
