#include <iostream>
#include <fstream>

int main()
{
    // Print Hello World and some sizes for demonstration
    std::cout << "Hello, World!" << std::endl;
    std::cout << "Size of std::string: " << sizeof(std::string) << " bytes" << std::endl;
    std::cout << "Size of long long: " << sizeof(long long) << " bytes" << std::endl;

    // Create or open the file temp.bin in binary output mode
    std::fstream disk_file;
    disk_file.open("temp.bin", std::ios::out | std::ios::binary);

    if (!disk_file) {
        std::cerr << "Error creating file!" << std::endl;
        return 1;
    }

    // Write some data to the file
    std::string data = "This is some binary data.";
    int number = 42;

    // Write the string length and the actual string (as binary)
    int strLength = data.length();
    disk_file.write(reinterpret_cast<char*>(&strLength), sizeof(int)); // Write string length
    disk_file.write(data.c_str(), strLength);                          // Write the string content

    // Write an integer value
    disk_file.write(reinterpret_cast<char*>(&number), sizeof(int));     // Write the integer value

    // Close the file to make sure data is flushed and persisted
    disk_file.close();

    std::cout << "Data written to temp.bin." << std::endl;

    return 0;
}
