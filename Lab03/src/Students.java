public class Students{
	private String studentId;
	private String name;
	private char gender;
	private String address;
public Students(String name, char gender, String address ){
	this.name = name;
	this.gender = gender;
	this.address = address;
}
private double count = 1;
public Students(){
	this.name = "";
	this.gender = 'M';
	this.address = "";
	this.studentId = "SP25-BCS-";

	}
public void display(){
System.out.println("Name: " + name + "\nGender: " + gender + "\nAddress: " + address + "\n---------------");
}
}