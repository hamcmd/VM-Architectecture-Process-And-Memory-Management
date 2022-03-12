package os_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class GPR {

    //Creating the array of general purpose registers
    //and flag register array.
    static short[] gpr = new short[16];
    static char[] flag = new char[16];

    GPR() {

    }

    //printing registers in decimal
    public void show_decimal_registers() {
        for (int i = 0; i < 16; i++) {
            System.out.println("R" + (i + 1) + " = " + gpr[i]);
        }
    }

    //printing registers in hexadecimal
    public void show_hex_registers() {
        for (int i = 0; i < 16; i++) {
            System.out.println("R" + (i + 1) + " = " + Integer.toHexString(gpr[i]));
        }
    }
}

class SPR {

    //Creating the array of special purpose registers
    short[] spr = new short[16];
}

class MEMORY {

    //creating a memory array
    static String[] mem_hex = new String[65536];
    //creating pointers
    static int cc;
    static int pc;
    static int cb = 0;

    MEMORY() {

    }
}

class operations {


    //reset overflow bit in flag register 
    public static void resetOverFlow() {
        GPR.flag[3] = '0';
    }

    //reset carry bit in flag register 
    public static void resetCarry() {
        GPR.flag[0] = '0';
    }
    //set zero bit in flag register 

    public static void setZero(short Rx) {
        if (Rx == 0) {
            GPR.flag[1] = '1';
        } else {
            GPR.flag[1] = '0';
        }
    }

    //set sign bit in flag register 
    public static void setSign(short Rx) {
        if (Rx < 0) {
            GPR.flag[2] = '1';
        } else {
            GPR.flag[2] = '0';
        }
    }

    //moving the value of second register into the first one
    public static void MOV(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        GPR.gpr[index1 - 1] = (short) GPR.gpr[index2 - 1];
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);

    }

    //saving the sum of both registers in first register
    public static void ADD(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        //if sum is greater than short then set the carry bit to 1
        if (GPR.gpr[index1 - 1] + GPR.gpr[index2 - 1] > Math.pow(2, 16)) {
            GPR.flag[3] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] + GPR.gpr[index2 - 1]);
            setZero(GPR.gpr[index1 - 1]);
            setSign(GPR.gpr[index1 - 1]);
        }
    }

    //subtracting the value of second register from the first one and saving the value in first one 
    public static void SUB(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] - GPR.gpr[index2 - 1]);
        //passing the final value to set the sign and zero bit to 1 accordingly
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }

    //multiplying the values in both registers and saving in first register
    public static void MUL(String Rx1, String Rx2) {
        
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        //if multiplication is greater than short then set the carry bit to 1
        if (GPR.gpr[index1 - 1] * GPR.gpr[index2 - 1] > Math.pow(2, 16)) {
            GPR.flag[3] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] * GPR.gpr[index2 - 1]);
            //passing the final value to set the sign and zero bit to 1 accordingly
            setZero(GPR.gpr[index1 - 1]);
            setSign(GPR.gpr[index1 - 1]);
        }
    }

    //dividing the value of first register with the second one and saving in first register
    public static void DIV(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] / GPR.gpr[index2 - 1]);
        //passing the final value to set the sign and zero bit to 1 accordingly
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }

    //ANDing the values in both registers and saving in first register
    public static void AND(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] & GPR.gpr[index2 - 1]);
        //passing the final value to set the sign and zero bit to 1 accordingly
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);

    }

    //ORing the values in both registers and saving in first register
    public static void OR(String Rx1, String Rx2) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx1);
        int index2 = Integer.parseInt(Rx2);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] | GPR.gpr[index2 - 1]);
        //passing the final value to set the sign and zero bit to 1 accordingly
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);

    }

    //moving the immediate in the specified register
    public static void MOVI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index = Integer.parseInt(Rx);
        GPR.gpr[index - 1] = num;
        //passing the final value to set zero bit to 1 accordingly
        setZero(GPR.gpr[index - 1]);
        setSign(GPR.gpr[index - 1]);
    }

    //adding the value of register and the immediate and saving the value in the same register
    public static void ADDI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        //setting the carry bit to 1 if value is greater than short
        if (GPR.gpr[index1 - 1] + num > Math.pow(2, 16)) {
            GPR.flag[3] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] + num);
            //setting the zero and sign bit to 1 according to the final value
            setZero(GPR.gpr[index1 - 1]);
            setSign(GPR.gpr[index1 - 1]);
        }
    }
    //subtracting the value of immediate from the register and saving the value in the same register

    public static void SUBI(String Rx, short num) {

        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] - num);
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }

    //multiplying the contents of the register with the immediate and 
    //saving the value in the same register
    public static void MULI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if (GPR.gpr[index1 - 1] * num > Math.pow(2, 16)) {
            GPR.flag[3] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] * num);
            setZero(GPR.gpr[index1 - 1]);
            setSign(GPR.gpr[index1 - 1]);
        }
    }

    //dividing the contents of the register with the immediate and 
    //saving the value in the same register
    public static void DIVI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] / num);
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }

    ////ANDing the contents of the register with the immediate and 
    //saving the value in the same register
    public static void ANDI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] & num);
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }
    
    //ORing the contents of the register with the immediate and 
    //saving the value in the same register
    public static void ORI(String Rx, short num) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] | num);
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);

    }
    
    //branch if the result of last operation resulted in a carry
    public static void BC(short num) {
        resetCarry();
        resetOverFlow();
        if (GPR.flag[0] == '1') {
            MEMORY.pc = MEMORY.cb + num;
        }

    }
    //branch if the result of last operation is zero
    public static void BZ(short num) {

        if (GPR.flag[1] == '1') {
            MEMORY.pc = MEMORY.cb + num;
        }
    }
    //branch if the result of last operation is not zero
    public static void BNZ(short num) {

        if (GPR.flag[1] == '0') {
            MEMORY.pc = MEMORY.cb + num;
        }

    }
    //branch if the result of last operation is less than zero
    public static void BS(short num) {

        if (GPR.flag[2] == '1') {
            MEMORY.pc = MEMORY.cb + num;
        }

    }
    //jump the program counter to a new instruction
    public static void JMP(short num) {
        resetCarry();
        resetOverFlow();
        MEMORY.pc = MEMORY.cb + num;
    }
    //method not defined properly in assignment instructions
    public static void ACT(short num) {
        resetCarry();
        resetOverFlow();
    }
    //picking a value from memory and saving it to a register
    public static void MOVL(String Rx) {
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = Short.valueOf(MEMORY.mem_hex[MEMORY.pc] + MEMORY.mem_hex[MEMORY.pc + 1]);
    }
    //picking a value from a register and saving it to memory
    public static void MOVS(String Rx) {
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        String s = Short.toString(GPR.gpr[index1 - 1]);
        String[] arr = new String[s.length()];
        //breaking the 2 byte value in two indexes each of one byte
        //to save the value propely in the memory
        for (int i = 0; i < s.length(); i++) {
            arr = s.split("");
        }
        String s1 = "";
        String s2 = "";
        if (arr.length == 1) {
            s1 = "00";
            s2 = "0" + arr[0];
        }
        if (arr.length == 2) {
            s1 = "00";
            s2 = arr[0] + arr[1];
        }
        if (arr.length == 3) {
            s1 = "0" + arr[0];
            s2 = arr[1] + arr[2];
        }
        if (arr.length == 4) {
            s1 = arr[0] + arr[1];
            s2 = arr[2] + arr[3];
        }
        //incrementing the code counter after adding new values
        MEMORY.mem_hex[MEMORY.cc + 1] = s1;
        MEMORY.cc++;
        MEMORY.mem_hex[MEMORY.cc + 2] = s2;
        MEMORY.cc++;
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);

    }
    //left shifitng the contents of a register by 1 bit
    public static void SHL(String Rx) {
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if (GPR.gpr[index1 - 1] > Math.pow(2, 16)) {
            GPR.flag[1] = '1';
        } else {

            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] >> 1);
            setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
        }
    }
    //right shifitng the contents of a register by 1 bit
    public static void SHR(String Rx) {
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if (GPR.gpr[index1 - 1] > Math.pow(2, 16)) {
            GPR.flag[1] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] << 1);
            setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
        }
    }
    //taking msb and shift the binary string to left and overwrite lsb by msb  
    public static void RTL(String Rx) {
        resetCarry();
        resetOverFlow();
        int index = Integer.parseInt(Rx);
        if (((GPR.gpr[index - 1] << 1) | (GPR.gpr[index - 1] >> (16 - 1))) > Math.pow(2, 16)) {
            GPR.flag[1] = '1';

        } else {
            GPR.gpr[index - 1] = (short) ((GPR.gpr[index - 1] << 1) | (GPR.gpr[index - 1] >> (16 - 1)));
            setZero(GPR.gpr[index - 1]);
        setSign(GPR.gpr[index - 1]);
        }
    }
    
    // taking lsb and shift the binary string to right and overwrite msb by lsb
    public static void RTR(String Rx) {
        resetCarry();
        resetOverFlow();
        int index = Integer.parseInt(Rx);
        
    if (((GPR.gpr[index - 1] >> 1) | (GPR.gpr[index - 1] << (16 - 1))) > Math.pow(2, 16)) {
            GPR.flag[1] = '1';

        } else {
           GPR.gpr[index - 1] = (short) ((GPR.gpr[index - 1] >> 1) | (GPR.gpr[index - 1] << (16 - 1)));
           setZero(GPR.gpr[index - 1]);
        setSign(GPR.gpr[index - 1]);
        }
    }

    //increasing register value by 1
    public static void INC(String Rx) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if (GPR.gpr[index1 - 1] + 1 > Math.pow(2, 16)) {
            GPR.flag[3] = '1';

        } else {
            GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] + 1);
            setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
        }
    }

    //decreasing register value by 1
    public static void DEC(String Rx) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = (short) (GPR.gpr[index1 - 1] - 1);
        setZero(GPR.gpr[index1 - 1]);
        setSign(GPR.gpr[index1 - 1]);
    }

    public static void NOOP() {
        resetCarry();
        resetOverFlow();
        //No Operation    
    }

    // terminating all processes
    public static void END() {
        resetCarry();
        resetOverFlow();
        System.out.println("Process Terminated");
        System.exit(0);
    }
}

public class OS_Project {

    public static void main(String[] args) {
        try {
            //created memory object
            MEMORY mem = new MEMORY();
            //read the file and saved it's contents in the memory
            Scanner in = new Scanner(new File("p1.txt"));
            while (in.hasNext()) {
                byte a = (byte) in.nextInt();
                //coverting integer into hexa decimal
                mem.mem_hex[mem.cc] = Integer.toHexString(a & 0xFF);
                //hex values --> 30 01 00 01 30 02 7f ff 19 01 02 f3
                mem.cc++;
            }
            
            String opcode = "";
            //setting program counter to the code base
            mem.pc = mem.cb;
            //while program counter is less thabn the code couter or it does not encounter terminate operation 
            //memory reading continues
            while (!(mem.pc >= mem.cc) || !(mem.mem_hex[mem.pc].equals("f3"))) {
                opcode = mem.mem_hex[mem.pc];
                switch (opcode) {
                    //whichever instruction is found in the memory, its switch case is run and program counter is
                    //incremmented accordingly
                    case "16":
                        mem.pc++;
                        operations.MOV(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        //pc is incrememnted by two to jump on the next instruction
                        mem.pc += 2;
                        break;
                    case "17":
                        mem.pc++;
                        operations.ADD(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "18":
                        mem.pc++;
                        operations.SUB(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "19":
                        mem.pc++;
                        operations.MUL(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "1A":
                        mem.pc++;
                        operations.DIV(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "1B":
                        mem.pc++;
                        operations.AND(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "1C":
                        mem.pc++;
                        operations.OR(mem.mem_hex[mem.pc], mem.mem_hex[mem.pc + 1]);
                        mem.pc += 2;
                        break;
                    case "30":
                        mem.pc++;
                        operations.MOVI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2], 16));
                        mem.pc += 3;
                        break;
                    case "31":
                        mem.pc++;
                        operations.ADDI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2]));
                        mem.pc += 3;
                        break;
                    case "32":
                        mem.pc++;
                        operations.SUBI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2]));
                        mem.pc += 3;
                        break;
                    case "33":
                        mem.pc++;
                        operations.MULI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2], 16));
                        mem.pc += 3;
                        break;
                    case "34":
                        mem.pc++;
                        operations.DIVI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2], 16));
                        mem.pc += 3;
                        break;
                    case "35":
                        mem.pc++;
                        operations.ANDI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2], 16));
                        mem.pc += 3;
                        break;
                    case "36":
                        mem.pc++;
                        operations.ORI(mem.mem_hex[mem.pc], (short) Integer.parseInt(mem.mem_hex[mem.pc + 1] + mem.mem_hex[mem.pc + 2]));
                        mem.pc += 3;
                        break;
                    case "37":
                        mem.pc++;
                        operations.BZ((short) Integer.parseInt(mem.mem_hex[mem.pc], 16));
                    case "38":
                        mem.pc++;
                        operations.BNZ((short) Integer.parseInt(mem.mem_hex[mem.pc], 16));
                    case "39":
                        mem.pc++;
                        operations.BC((short) Integer.parseInt(mem.mem_hex[mem.pc], 16));
                    case "3A":
                        mem.pc++;
                        operations.BS((short) Integer.parseInt(mem.mem_hex[mem.pc], 16));
                    case "3B":
                        mem.pc++;
                        operations.JMP((short) Integer.parseInt(MEMORY.mem_hex[MEMORY.pc], 16));
                        mem.pc++;
                    case "3D":
                        mem.pc++;
                        operations.ACT((short) (Integer.parseInt(mem.mem_hex[mem.pc])));
                        mem.pc++;
                    case "51":
                        mem.pc++;
                        operations.MOVL(mem.mem_hex[mem.pc]);
                        mem.pc++;
                    case "52":
                        mem.pc++;
                        operations.MOVS(mem.mem_hex[mem.pc]);
                        mem.pc++;
                    case "71":
                        mem.pc++;
                        operations.SHL(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "72":
                        mem.pc++;
                        operations.SHR(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "73":
                        mem.pc++;
                        operations.RTL(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "74":
                        mem.pc++;
                        operations.RTR(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "75":
                        mem.pc++;
                        operations.INC(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "76":
                        mem.pc++;
                        operations.DEC(mem.mem_hex[mem.pc]);
                        mem.pc += 1;
                        break;
                    case "f2":
                        operations.NOOP();
                        mem.pc++;
                    case "f3":
                        GPR gpr = new GPR();
                        System.out.println("General Purpose Registers\nDecimal Registers");
                        gpr.show_decimal_registers();
                        System.out.println("Hex Registers");
                        gpr.show_hex_registers();
                        System.out.println("\nSpecial Purpose Registers");
                        SPR sprs = new SPR();
                        sprs.spr[0] = (short) MEMORY.cb;
                        sprs.spr[2] = (short) MEMORY.cc;
                        sprs.spr[9] = (short) MEMORY.pc;
                        sprs.spr[10] = (short) Integer.parseInt(opcode, 16);
                        for (int i = 0; i < sprs.spr.length; i++) {
                            System.out.println(sprs.spr[i]);

                        }
                        operations.END();
                }
            }
            GPR gpr = new GPR();
            System.out.println("General Purpose Registers\nDecimal Registers");
            gpr.show_decimal_registers();
            System.out.println("\nHex Registers");
            gpr.show_hex_registers();
            System.out.println("\nSpecial Purpose Registers");
            SPR sprs = new SPR();
            sprs.spr[0] = (short) MEMORY.cb;
            sprs.spr[2] = (short) MEMORY.cc;
            sprs.spr[9] = (short) MEMORY.pc;
            sprs.spr[10] = (short) Integer.parseInt(opcode, 16);
            for (int i = 0; i < sprs.spr.length; i++) {
                System.out.println();
                System.out.println(sprs.spr[i]);

            }
           //if the file is not found, catch and print the exception.
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OS_Project.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);

        }

    }

}
