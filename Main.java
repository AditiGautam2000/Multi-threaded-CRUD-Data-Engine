import java.util.*;
class Main{

    public static void main(String args[]){

        Scanner sc=new Scanner(System.in);
        
        System.out.println("Press 1 to create an entry");
        System.out.println("Press 2 to read an entry");
        System.out.println("Press 3 to update an entry");
        System.out.println("Press 4 to delete an entry");

        int choice=sc.nextInt();

        

        switch(choice){

            case 1: Create create=new Create();
                    create.createUser();   
                    break;
            case 2: Read read=new Read();
                    read.readuser();
                       break;
            case 3:  Update update=new Update();
                    update.updateUsers();
                      break;
            case 4:   Delete delete=new Delete();
                      delete.deleteUser();
                       break;
            default: System.out.println("Enter correct choice");
        }

    }
}