#!/bin/bash
# Start all microservices with PostgreSQL

WORKSPACE="/Users/charles/Desktop/usyd/5348/Tutorial-01-Group-08"
BUILD_SUCCESS=true

echo "🚀 E-Commerce Microservices Startup"
echo "====================================="
echo "Workspace: $WORKSPACE"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "✅ Checking prerequisites..."

if ! command_exists mvn; then
    echo "❌ Maven not found. Please install Maven."
    exit 1
fi

if ! command_exists psql; then
    echo "❌ PostgreSQL not found. Please install PostgreSQL."
    exit 1
fi

# Check PostgreSQL service status
echo "✅ Checking PostgreSQL service..."
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "⚠️  PostgreSQL not running. Attempting to start..."
    brew services start postgresql@15 2>/dev/null
    sleep 2
    
    if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
        echo "❌ Failed to start PostgreSQL"
        exit 1
    fi
fi

echo "✅ PostgreSQL is running"
echo ""

# Build project
echo "🔨 Building project..."
cd "$WORKSPACE"

if ! mvn clean install -q -DskipTests; then
    echo "❌ Build failed"
    BUILD_SUCCESS=false
fi

if [ "$BUILD_SUCCESS" = true ]; then
    echo "✅ Build successful"
    echo ""
    echo "📝 To start all services, run these in separate terminals:"
    echo ""
    echo "Terminal 1 (Store API - Port 8080):"
    echo "  cd $WORKSPACE/store-api && mvn spring-boot:run"
    echo ""
    echo "Terminal 2 (Bank Service - Port 8082):"
    echo "  cd $WORKSPACE/bank && mvn spring-boot:run"
    echo ""
    echo "Terminal 3 (Warehouse Service - Port 8081):"
    echo "  cd $WORKSPACE/warehouse-service && mvn spring-boot:run"
    echo ""
    echo "Terminal 4 (Delivery Service - Port 8083):"
    echo "  cd $WORKSPACE/DeliveryGo && mvn spring-boot:run"
    echo ""
    echo "Terminal 5 (Order Orchestrator - Port 8084):"
    echo "  cd $WORKSPACE/order-orchestrator && mvn spring-boot:run"
    echo ""
    echo "Terminal 6 (Email Service - Port 8085):"
    echo "  cd $WORKSPACE/email-svc && mvn spring-boot:run"
    echo ""
    echo "====================================="
    echo "Or use individual start scripts in the root directory"
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
