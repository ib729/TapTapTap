#!/bin/bash

echo "Installing TapTapTap..."

# Create directory
mkdir -p ~/myapps/taptaptap

# Copy JAR file
cp TapTapTap.jar ~/myapps/taptaptap/

# Detect OS and install accordingly
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sudo tee /usr/local/bin/ttt << 'EOF'
#!/bin/bash
java -jar ~/myapps/taptaptap/TapTapTap.jar "$@"
EOF
    sudo chmod +x /usr/local/bin/ttt
    echo "TapTapTap installed successfully! Try 'ttt' to get started."
else
    # Linux
    sudo tee /usr/bin/ttt << 'EOF'
#!/bin/bash
java -jar ~/myapps/taptaptap/TapTapTap.jar "$@"
EOF
    sudo chmod +x /usr/bin/ttt
    echo "TapTapTap installed successfully! Try 'ttt' to get started."
fi