package utilities;

public class Validator {

    /**
     * Validates the company ID format
     * @param id
     * @return
     */
    public static boolean companyId(String id){

        try{
            if(id.length() < 1 || id.length() < 2){
                System.err.println("Company id too short. Please try C382");
                return false;
            }

            if(! (id.substring(0,1)).equalsIgnoreCase("c")){
                System.err.println("Must have suffix 'C' followed by a valid number");
                return false;
            }
        }catch (Exception e){
            return false;
        }

        // Rest of the part must be numeric
        try{
            Integer.parseInt( id.substring(1) );
        }catch (Exception e){
            System.err.println(id.substring(1)+" is not a number.");
            return false;
        }

        return true;
    }


    /**
     * Validates the project owners id format
     * @param id
     * @return
     */
    public static boolean projectOwnerId(String id)
    {
        try{
            // Length validation - after suffix must have numeric character
            if(id.length() <= 3){
                System.err.println("Must have suffix 'Own' followed by a valid number");
                return false;
            }

            // Suffix validation - must have suffix own
            if(!(id.substring(0,3)).equalsIgnoreCase("own")){
                System.err.println("Must have suffix 'Own'");
                return false;
            }
        }catch (Exception e){
            return false;
        }

        // Rest of the part must be numeric
        try{
            Integer.parseInt( id.substring(3) );
        }catch (Exception e){
            System.err.println(id.substring(3)+" is not a number.");
            return false;
        }

        return true;
    }


    /**
     * Validates the project ID
     * @param id
     * @return
     */
    public static boolean projectId(String id){

        if(id.length() <= 1){
            System.err.println(id+" is to short to be project id");
            return false;
        }

        // Suffix validation
        if(! id.substring(0,2).contentEquals("Pr")){
            System.err.println("Project must contain the Pr suffix, followed by numbers");
            return false;
        }

        // Rest of the part must be numeric
        try{
            Integer.parseInt( id.substring(2) );
        }catch (Exception e){
            System.err.println(id.substring(2)+" is not a number.");
            return false;
        }

        return true;
    }
}
