package nuitinfo.whiletrue.bot;

public class Bot {

    public static void main(String[] args){
        System.out.println("Hello world!");
        if(args.length == 1)
            System.out.println("Le token est " + args[0]);
        else
            System.out.println("Un seul paramètre était attendu, " + args.length + "ont été reçus.");
    }

}
