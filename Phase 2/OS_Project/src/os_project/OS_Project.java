package os_project;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.E;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static os_project.process.decimalToHex;

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

    public void set_gpr(int index, short value) {
        gpr[index] = value;
    }

    public short get_gpr(int index) {
        return gpr[index];

    }
}

class SPR {

    MEMORY mem;
    //Creating the array of special purpose registers
    short[] spr = new short[16];

    SPR(MEMORY mem) {
        this.mem = mem;
    }

    public void set_spr(int index, short value) {
        spr[index] = value;
    }

    public short get_spr(int index) {
        return spr[index];

    }
}

class MEMORY {

    //creating a memory array
    static String[] mem_hex = new String[65536];
    static byte[] byteMemory = new byte[65536];
    //creating pointers
    static int cc = 0;
    static int pc;
    static int cb = 0;

    int currentpage;

    static Stack<String> stack = new Stack<>();

    MEMORY() {

    }

    public void addProcess(byte[] dataArray, byte[] codeArray, int dataPages, int codePages) {
        int temp = 0;
        System.out.println(dataArray.length);
        for (int i = 0; i < dataPages; i++) {
            cc = 128 * currentpage;
            if (temp == dataPages - 1) {
                if (dataArray.length % 128 == 0) {
                    for (int j = 0; j < 128; j++) {
                        byteMemory[cc] = dataArray[(temp * 128) + j];
                        cc++;
                    }
                } else {
                    for (int j = 0; j < dataArray.length % 128; j++) {
                        byteMemory[cc] = dataArray[(temp * 128) + j];
                        cc++;
                    }
                }

            } else {
                for (int j = 0; j < 128; j++) {
                    byteMemory[cc] = dataArray[(temp * 128) + j];
                    cc++;
                }
            }
            temp++;
            currentpage++;
        }

        temp = 0;

        for (int i = 0; i < codePages; i++) {
            cc = 128 * currentpage;
            if (temp == codePages - 1) {
                if (codeArray.length % 128 == 0) {
                    for (int j = 0; j < 128; j++) {
                        byteMemory[cc] = codeArray[(temp * 128) + j];
                        cc++;
                    }
                } else {
                    for (int j = 0; j < codeArray.length % 128; j++) {
                        byteMemory[cc] = codeArray[(temp * 128) + j];
                        cc++;
                    }
                }

            } else {
                for (int j = 0; j < 128; j++) {
                    byteMemory[cc] = codeArray[(temp * 128) + j];
                    cc++;
                }
            }
            temp++;
            currentpage++;
        }

        System.out.println("Pages occupied: " + currentpage);
    }

    public int getCurrentpage() {
        return currentpage;
    }
}

class operations {
    static String check = "";

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
    public static void BC(process p, short num) {
        resetCarry();
        resetOverFlow();
        if (GPR.flag[0] == '1') {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
        } else {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] += 3;
        }

    }

    //branch if the result of last operation is zero
    public static void BZ(process p, short num) {

        if (GPR.flag[1] == '1') {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
        } else {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] += 3;
        }
    }

    //branch if the result of last operation is not zero
    public static void BNZ(process p, short num) {

        if (GPR.flag[1] == '0') {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
        } else {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] += 3;
        }

    }

    //branch if the result of last operation is less than zero
    public static void BS(process p, short num) {

        if (GPR.flag[2] == '1') {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
        } else {
            p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] += 3;
        }

    }

    //jump the program counter to a new instruction
    public static void JMP(process p, short num) {
        resetCarry();
        resetOverFlow();
        p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
    }

    public static void CALL(process p, short num) {
        resetCarry();
        resetOverFlow();
        MEMORY.stack.push("PC" + String.valueOf(p.mem.mem_hex[p.pcb.y.spr[9]]));
        p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] = p.mem.mem_hex[p.pcb.y.spr[0]] + num;
    }

    //method not defined properly in assignment instructions
    public static void ACT(short num) {
        resetCarry();
        resetOverFlow();
    }

    //picking a value from memory and saving it to a register
    public static void MOVL(process p, String Rx) {
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        GPR.gpr[index1 - 1] = Short.valueOf( p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]] +  p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
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

    public static void PUSH(String Rx) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if(MEMORY.stack.capacity() == 50){
            check = "overflow";
        }
        else
        MEMORY.stack.push(Rx);
        
    }

    public static void POP(String Rx) {
        resetCarry();
        resetOverFlow();
        int index1 = Integer.parseInt(Rx);
        if(MEMORY.stack.capacity() == 0){
            check = "underflow";
        }
        else
        Rx = MEMORY.stack.pop();
    }
//    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
//        ///source for regex --> https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(addemailTF.getText());
//        if (matcher.matches() && !addtitleTF.getText().equals("") && !addsubtitleTF.getText().equals(""))

    public static void RETURN(process p) {
        
        ArrayList<String> arrls = new ArrayList<>();
        
        String regex = "^PC.";
        Matcher matcher;
        int index = -1; 
        Pattern pattern = Pattern.compile(regex);
        for (int i = MEMORY.stack.capacity() - 1; i >= 0; i--) {
            matcher = pattern.matcher(MEMORY.stack.elementAt(i));
            if (matcher.matches()) {
                index = i;
                break;
            }
        }
        for (int i = 0; i <= index; i++) {
            if (i == index) {
                String temp = MEMORY.stack.pop();
                StringBuilder sb = new StringBuilder(temp);
                sb.deleteCharAt(0); //P
                sb.deleteCharAt(1); //C
                p.pcb.y.spr[9] =(short) Integer.parseInt(sb.toString());
                break;
            } else {
                arrls.add(MEMORY.stack.pop());
            }

        }
        for (int i = arrls.size() - 1; i >= 0; i++) {
            MEMORY.stack.push(arrls.get(i));
        }
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

class page_table {

    String[] page_tab = new String[512];
    int[] flag = new int[512];
    int size = 0;

    page_table() {

    }

    page_table(int size) {
        page_tab = null;
        flag = null;
        page_tab = new String[this.size];
        flag = new int[this.size];
    }

    public String get_frame(int page_number) {
        return page_tab[page_number];
    }

    public void set_frame(String frame_number, int page_number) {
        page_tab[page_number] = frame_number;
        flag[page_number] = 1;
    }

    public int get_flag(int index) {
        return flag[index];
    }

    public void print_page_table() {
        for (int i = 0; i < page_tab.length; i++) {
            if (flag[i] != 0) {
                System.out.println("Page " + i + "=" + page_tab[i]);

            }

        }
    }
}

class PCB {

    String process_id;
    int process_size;
    int process_priority;
    String process_file_name;
    int datasize;
    int codesize;
    GPR x;
    SPR y;

    int codepages;
    int datapages;
    int time;
    PageTable data;
    PageTable code;

    public PCB(int process_priority, String process_id, int process_size, String process_file_name, int datapages, int codepages, MEMORY mem) {
        this.process_priority = process_priority;
        this.process_id = process_id;
        this.process_size = process_size;
        this.process_file_name = process_file_name;
        data = new PageTable(datapages);
        code = new PageTable(codepages);
        x = new GPR();
        y = new SPR(mem);
        time = 0;
    }

    public void setProcess_id(String process_id) {
        this.process_id = process_id;
    }

    public void setProcess_size(int process_size) {
        this.process_size = process_size;
    }

    public void setProcess_priority(int process_priority) {
        this.process_priority = process_priority;
    }

    public void setDatasize(int datasize) {
        this.datasize = datasize;
    }

    public void setCodesize(int codesize) {
        this.codesize = codesize;
    }

    public void setCodepages(int codepages) {
        this.codepages = codepages;
    }

    public void setDatapages(int datapages) {
        this.datapages = datapages;
    }

    public void setCode(PageTable code) {
        this.code = code;
    }

    public String getProcess_id() {
        return process_id;
    }

    public int getProcess_size() {
        return process_size;
    }

    public int getProcess_priority() {
        return process_priority;
    }

    public String getProcess_file_name() {
        return process_file_name;
    }

    public int getDatasize() {
        return datasize;
    }

    public int getCodesize() {
        return codesize;
    }

    public int getCodepages() {
        return codepages;
    }

    public int getDatapages() {
        return datapages;
    }

    public int getTime() {
        return time;
    }

    public PageTable getData() {
        return data;
    }

    public PageTable getCode() {
        return code;
    }

}

class PageTable {

    int[] pages;
    int[] flag; //0 would mean empty, 1 would mean dataPage, 2 would mean codePage

    public PageTable(int numOfPages) {
        pages = new int[numOfPages];
        flag = new int[numOfPages];
    }
}

class process {

    int datapages;
    int codepages;
    int datasize;
    int codesize;
    MEMORY mem = new MEMORY();
    static Stack<String> stack = new Stack<>();

    public static String decimalToHex(int decimal) {
        int remainder;
        String hex = "";
        char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        if (decimal == 0) {
            return hex = "00";
        }
        while (decimal > 0) {
            remainder = decimal % 16;
            if (decimal / 16 == 0 && hex.equals("")) {
                hex = "0" + hexChar[remainder] + hex;
            } else {
                hex = hexChar[remainder] + hex;
            }
            decimal = decimal / 16;
        }
        return hex;
    }

    Path path;
    byte[] bytes;

    ArrayList<String> process1 = new ArrayList<>();
    PCB pcb;
    page_table pt;

    //int datasize;
    String temp_datasize = "";
    String temp_processid = "";
    String name = "";

    process(MEMORY mem, String file_name) throws FileNotFoundException, IOException {
        name = file_name;
        path = Paths.get(file_name);
        bytes = Files.readAllBytes(path);
        //System.out.println("bytes length"+bytes.length);
        
        String[] byte_string = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byte_string[i] = decimalToHex(Byte.toUnsignedInt(bytes[i]));
        }
        
        int process_priority = bytes[0];
        temp_datasize = byte_string[3] + byte_string[4];
//        for (int i = 0; i < byte_string.length; i++) {
//            System.out.println(Byte.toUnsignedInt(bytes[i]));
//            System.out.println("byte_string " + byte_string[i]);
//        }
        //System.out.println("temp " + temp);
        datasize = Integer.parseInt(temp_datasize, 16);
//        System.out.println("data " + pcb.datasize);

        temp_processid = byte_string[1] + byte_string[2];
        String process_id = String.valueOf(Integer.parseInt(temp_processid, 16));

        codesize = bytes.length - datasize - 8;
        
        int process_size = bytes.length;
        System.out.println("");
        
        
        
        
        
        pcb = new PCB(process_priority, process_id, process_size, file_name, dataPages(datasize), codePages(codesize), mem);
        pt = new page_table(datasize + codesize + 8);
        codepages = codePages(codesize);
        datapages = dataPages(datasize);
        int pagePointer = mem.getCurrentpage();
        for (int i = 0; i < dataPages(datasize); i++) {
            pcb.data.pages[i] = pagePointer;
            //System.out.print("DataPages " + pcb.data.pages[i] + "  ");
            //System.out.println("");
            pagePointer++;
        }
        for (int i = 0; i < codepages; i++) {
            pcb.code.pages[i] = pagePointer;
            //System.out.print("CodePages " + pcb.code.pages[i] + "  ");
            //System.out.println("");
            pagePointer++;
        }

        byte[] dataArray = makeDataArray(bytes);
        byte[] codeArray = makeCodeArray(bytes);

        if (codepages != 0) {
            pcb.y.spr[0] = (short) (pcb.code.pages[0] * 128);//codebase
            pcb.y.spr[1] = (short) ((pcb.code.pages[pcb.code.pages.length - 1] * 128) + 128);//codebase
            if (codeArray.length % 128 == 0) {
                pcb.y.spr[2] = (short) ((pcb.code.pages[pcb.code.pages.length - 1] * 128) + 128);//codecounter
            } else {
                pcb.y.spr[2] = (short) ((pcb.code.pages[pcb.code.pages.length - 1] * 128) + (codeArray.length % 128));//codecounter
            }
        }
        if (datapages != 0) {
            pcb.y.spr[3] = (short) (pcb.data.pages[0] * 128);
            pcb.y.spr[4] = (short) ((pcb.data.pages[pcb.data.pages.length - 1] * 128) + 128);
            if (dataArray.length % 128 == 0) {
                pcb.y.spr[5] = (short) ((pcb.data.pages[pcb.data.pages.length - 1] * 128) + 128);
            } else {
                pcb.y.spr[5] = (short) ((pcb.data.pages[pcb.data.pages.length - 1] * 128) + (dataArray.length % 128));
            }
        }

        //System.out.println(pcb.y.spr[0] + "  " + pcb.y.spr[1] + "  " + pcb.y.spr[2] + "  " + pcb.y.spr[3] + "  " + pcb.y.spr[4] + "  " + pcb.y.spr[5]);
        mem.addProcess(dataArray, codeArray, datapages, codepages);

        //print_file();
    }

    public static int dataPages(int dataSize) {
        if (dataSize % 128 == 0) {
            return dataSize / 128;
        } else {
            return ((dataSize / 128) + 1);
        }
    }

    public static int codePages(int codeSize) {
        if (codeSize % 128 == 0) {
            return codeSize / 128;
        } else {
            return ((codeSize / 128) + 1);
        }
    }

    public byte[] makeDataArray(byte[] bytes) {
        byte[] array = new byte[datasize];
        for (int i = 0; i < datasize; i++) {
            array[i] = bytes[i + 8];
        }
        return array;
    }

    public byte[] makeCodeArray(byte[] bytes) {
        byte[] array = new byte[codesize];
        for (int i = 0; i < codesize; i++) {
            array[i] = bytes[i + 8 + datasize];
        }
        return array;
    }

    ArrayList to_hex() {
        for (int i = 0; i < bytes.length; i++) {
            process1.add(decimalToHex(Byte.toUnsignedInt(bytes[i])));
        }
        return process1;
    }

    void print_file() {
        System.out.println("Filename: " + name + " Process ID: " + pcb.process_id + " Process Priority: " + pcb.process_priority
                + " Process Size: " + pcb.process_size);
        System.out.println("Code Size: " + codesize + " Data Size: " + datasize);
        System.out.print("Total Data Pages: " + datapages + " Total Code Pages: " + codepages);
        for (int i = 0; i < bytes.length; i++) {
            if (i % 200 == 0) {
                System.out.println("");
            }
            System.out.print(Byte.toUnsignedInt(bytes[i]) + " ");
        }
        System.out.println("\n\n");

    }

}

public class OS_Project {

    public static void execute(process p) {

        //setting program counter to the code base
        //while program counter is less thabn the code couter or it does not encounter terminate operation 
        //memory reading continues
        p.pcb.y.spr[9] = p.pcb.y.spr[0];
        int count = 0;

        while (!(p.pcb.y.spr[9] >= p.pcb.y.spr[2])) {
            //System.out.println("Iteration " + count);
            count++;
            String opcode = p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]];
            //System.out.println("opcode " + opcode);
            switch (opcode) {
                //whichever instruction is found in the memory, its switch case is run and program counter is
                //incremmented accordingly
                case "16":
                    p.pcb.y.spr[9]++;
                    operations.MOV(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    //pc is incrememnted by two to jump on the next instruction
                    p.pcb.y.spr[9] += 2;
                    break;
                case "17":
                    p.pcb.y.spr[9]++;
                    operations.ADD(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "18":
                    p.pcb.y.spr[9]++;
                    operations.SUB(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "19":
                    p.pcb.y.spr[9]++;
                    operations.MUL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1A":
                    p.pcb.y.spr[9]++;
                    operations.DIV(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1B":
                    p.pcb.y.spr[9]++;
                    operations.AND(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1C":
                    p.pcb.y.spr[9]++;
                    operations.OR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "30":
                    p.pcb.y.spr[9]++;
                    operations.MOVI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "31":
                    p.pcb.y.spr[9]++;
                    operations.ADDI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "32":
                    p.pcb.y.spr[9]++;
                    operations.SUBI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "33":
                    p.pcb.y.spr[9]++;
                    operations.MULI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "34":
                    p.pcb.y.spr[9]++;
                    operations.DIVI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "35":
                    p.pcb.y.spr[9]++;
                    operations.ANDI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "36":
                    p.pcb.y.spr[9]++;
                    operations.ORI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "37":
                    p.pcb.y.spr[9]++;
                    operations.BZ(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "38":
                    p.pcb.y.spr[9]++;
                    operations.BNZ(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "39":
                    p.pcb.y.spr[9]++;
                    operations.BC(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "3a":
                    p.pcb.y.spr[9]++;
                    operations.BS(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "3b":
                    p.pcb.y.spr[9]++;
                    operations.JMP(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    p.pcb.y.spr[9]++;
                    break;
                case "3c":
                    p.pcb.y.spr[9]++;
                    //operations.CALL(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9]++;
                    break;    
                case "3d":
                    p.pcb.y.spr[9]++;
                    operations.ACT((short) (Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]])));
                    p.pcb.y.spr[9]++;
                    break;
                case "51":
                    p.pcb.y.spr[9]++;
                    operations.MOVL(p, p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9]+=3;
                    break;
                case "52":
                    p.pcb.y.spr[9]++;
                    operations.MOVS(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9]+=3;
                    break;
                case "71":
                    p.pcb.y.spr[9]++;
                    operations.SHL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "72":
                    p.pcb.y.spr[9]++;
                    operations.SHR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "73":
                    p.pcb.y.spr[9]++;
                    operations.RTL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "74":
                    p.pcb.y.spr[9]++;
                    operations.RTR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "75":
                    p.pcb.y.spr[9]++;
                    operations.INC(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "76":
                    p.pcb.y.spr[9]++;
                    operations.DEC(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "77":
                    p.pcb.y.spr[9]++;
                    //operations.PUSH(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;  
                case "78":
                    p.pcb.y.spr[9]++;
                    //operations.POP(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break; 
                 case "f1":
                    //operations.RETURN(p);
                    p.pcb.y.spr[9]++;
                    break;   
                case "f2":
                    operations.NOOP();
                    p.pcb.y.spr[9]++;
                    break;
                case "f3":
                    GPR gpr = new GPR();
                    System.out.println("General Purpose Registers\nDecimal Registers");
                    gpr.show_decimal_registers();
                    System.out.println("Hex Registers");
                    gpr.show_hex_registers();
                    System.out.println("\nSpecial Purpose Registers");
                    SPR sprs = new SPR(p.mem);
                    sprs.spr[0] = (short) MEMORY.cb;
                    sprs.spr[2] = (short) MEMORY.cc;
                    sprs.spr[9] = (short) MEMORY.pc;
                    sprs.spr[10] = (short) Integer.parseInt(opcode, 16);
                    for (int i = 0; i < sprs.spr.length; i++) {
                        System.out.println(sprs.spr[i]);

                    }
//                default:
//                    System.exit(0);
//                    operations.END();
            }
        }
    }
        public static void executeRobin(process p) {

        //setting program counter to the code base
        //while program counter is less thabn the code couter or it does not encounter terminate operation 
        //memory reading continues
        int iCount =0;
        String check = "";
        
            
        p.pcb.y.spr[9] = p.pcb.y.spr[0];
        int count = 0;

        while (!(p.pcb.y.spr[9] >= p.pcb.y.spr[2])) {
            //System.out.println("Iteration " + count);
            count++;
            String opcode = p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]];
            //System.out.println("opcode " + opcode);
            switch (opcode) {
                //whichever instruction is found in the memory, its switch case is run and program counter is
                //incremmented accordingly
                case "16":
                    p.pcb.y.spr[9]++;
                    operations.MOV(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    //pc is incrememnted by two to jump on the next instruction
                    p.pcb.y.spr[9] += 2;
                    break;
                case "17":
                    p.pcb.y.spr[9]++;
                    operations.ADD(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "18":
                    p.pcb.y.spr[9]++;
                    operations.SUB(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "19":
                    p.pcb.y.spr[9]++;
                    operations.MUL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1A":
                    p.pcb.y.spr[9]++;
                    operations.DIV(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1B":
                    p.pcb.y.spr[9]++;
                    operations.AND(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "1C":
                    p.pcb.y.spr[9]++;
                    operations.OR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1]);
                    p.pcb.y.spr[9] += 2;
                    break;
                case "30":
                    p.pcb.y.spr[9]++;
                    operations.MOVI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "31":
                    p.pcb.y.spr[9]++;
                    operations.ADDI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "32":
                    p.pcb.y.spr[9]++;
                    operations.SUBI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "33":
                    p.pcb.y.spr[9]++;
                    operations.MULI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "34":
                    p.pcb.y.spr[9]++;
                    operations.DIVI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "35":
                    p.pcb.y.spr[9]++;
                    operations.ANDI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2], 16));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "36":
                    p.pcb.y.spr[9]++;
                    operations.ORI(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9] += 3;
                    break;
                case "37":
                    p.pcb.y.spr[9]++;
                    operations.BZ(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "38":
                    p.pcb.y.spr[9]++;
                    operations.BNZ(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "39":
                    p.pcb.y.spr[9]++;
                    operations.BC(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "3a":
                    p.pcb.y.spr[9]++;
                    operations.BS(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    break;
                case "3b":
                    p.pcb.y.spr[9]++;
                    operations.JMP(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]], 16));
                    p.pcb.y.spr[9]++;
                    break;
                case "3c":
                    p.pcb.y.spr[9]++;
                    //operations.CALL(p, (short) Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 1] + p.pcb.y.mem.mem_hex[p.pcb.y.spr[9] + 2]));
                    p.pcb.y.spr[9]++;
                    break;    
                case "3d":
                    p.pcb.y.spr[9]++;
                    operations.ACT((short) (Integer.parseInt(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]])));
                    p.pcb.y.spr[9]++;
                    break;
                case "51":
                    p.pcb.y.spr[9]++;
                    operations.MOVL(p, p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9]+=3;
                    break;
                case "52":
                    p.pcb.y.spr[9]++;
                    operations.MOVS(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9]+=3;
                    break;
                case "71":
                    p.pcb.y.spr[9]++;
                    operations.SHL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "72":
                    p.pcb.y.spr[9]++;
                    operations.SHR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "73":
                    p.pcb.y.spr[9]++;
                    operations.RTL(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "74":
                    p.pcb.y.spr[9]++;
                    operations.RTR(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "75":
                    p.pcb.y.spr[9]++;
                    operations.INC(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "76":
                    p.pcb.y.spr[9]++;
                    operations.DEC(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;
                case "77":
                    p.pcb.y.spr[9]++;
                    //operations.PUSH(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break;  
                case "78":
                    p.pcb.y.spr[9]++;
                    //operations.POP(p.pcb.y.mem.mem_hex[p.pcb.y.spr[9]]);
                    p.pcb.y.spr[9] += 1;
                    break; 
                 case "f1":
                    //operations.RETURN(p);
                    p.pcb.y.spr[9]++;
                    break;   
                case "f2":
                    operations.NOOP();
                    p.pcb.y.spr[9]++;
                    break;
                case "f3":
                    GPR gpr = new GPR();
                    System.out.println("General Purpose Registers\nDecimal Registers");
                    gpr.show_decimal_registers();
                    System.out.println("Hex Registers");
                    gpr.show_hex_registers();
                    System.out.println("\nSpecial Purpose Registers");
                    SPR sprs = new SPR(p.mem);
                    sprs.spr[0] = (short) MEMORY.cb;
                    sprs.spr[2] = (short) MEMORY.cc;
                    sprs.spr[9] = (short) MEMORY.pc;
                    sprs.spr[10] = (short) Integer.parseInt(opcode, 16);
                    for (int i = 0; i < sprs.spr.length; i++) {
                        System.out.println(sprs.spr[i]);

                    }
                default:
                    System.exit(0);
                    operations.END();
                    
                    
            }
            iCount++;
            if(iCount % 4 == 0){
                break;
            }
        }
        
    
        /*
        GPR gpr = new GPR();
        System.out.println("General Purpose Registers\nDecimal Registers");
        gpr.show_decimal_registers();
        System.out.println("\nHex Registers");
        gpr.show_hex_registers();
        System.out.println("\nSpecial Purpose Registers");
        SPR sprs = new SPR(p.mem);
        sprs.spr[0] = (short) MEMORY.cb;
        sprs.spr[2] = (short) MEMORY.cc;
        sprs.spr[9] = (short) MEMORY.pc;
        sprs.spr[10] = (short) Integer.parseInt(opcode, 16);
        for (int i = 0; i < sprs.spr.length; i++) {
            System.out.println();
            System.out.println(sprs.spr[i]);

        }*/

        //if the file is not found, catch and print the exception.
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //PCB pcb = new PCB();
        MEMORY mem = new MEMORY();
        Scheduler s = new Scheduler();

        process p1 = new process(mem, "flags");
        p1.to_hex();
        p1.print_file();
        s.addProcess(p1);

        //System.out.println("datasize" + p1.datasize);
        //page_table pt = new page_table(pcb.codesize + pcb.datasize + 8);
        System.out.println("");
        process p2 = new process(mem, "large0");
        p2.to_hex();
        p2.print_file();
        s.addProcess(p2);

        System.out.println("");
        process p3 = new process(mem, "noop");
        p3.to_hex();
        p3.print_file();
        s.addProcess(p3);

        System.out.println("");
        process p4 = new process(mem, "p5");
        p4.to_hex();
        p4.print_file();
        s.addProcess(p4);
        System.out.println("");

        process p5 = new process(mem, "power");
        p5.to_hex();
        p5.print_file();
        s.addProcess(p5);
        System.out.println("");

        process p6 = new process(mem, "sfull");
        p6.to_hex();
        p6.print_file();
        s.addProcess(p6);

        //System.out.println("\ndatasize" + pcb.datasize);
        /*
            for (int i = 8; i < pcb.datasize; i++) {
                pt.page_tab[i] = p1.process1.get(i);
            }
            for (int i = pcb.datasize; i < p1.process1.size() - pcb.datasize -8; i++) {
                pt.page_tab[i] = p1.process1.get(i);
            }
            //pt.print_page_table();
         */
        //created memory object
        //read the file and saved it's contents in the memory
        /*
        Scanner in = new Scanner(new File("p1.txt"));
        while (in.hasNext()) {
            byte a = (byte) in.nextInt();
            //coverting integer into hexa decimal
            mem.mem_hex[mem.cc] = Integer.toHexString(a & 0xFF);
            //hex values --> 30 01 00 01 30 02 7f ff 19 01 02 f3
            mem.cc++;
        }
         */
        PrintWriter output;
//        String s = "";
//        for (int i = 0; i < this.mem.byteMemory.length; i++) {
//            s += i + "\t" + mem.byteMemory[i] +"\n"; 
//        }
//        output = new PrintWriter(new FileOutputStream("src\\output.txt", false));
//            output.write(s);
//            output.close();
        String var = "";
        for (int i = 0; i < mem.byteMemory.length; i++) {
            mem.mem_hex[i] = Integer.toHexString(mem.byteMemory[i] & 0xFF);
            var +=  i + "\t" + mem.mem_hex[i] +"\n"; 
            //System.out.println(mem.mem_hex[i] + "   " + i);
        }
        output = new PrintWriter(new FileOutputStream("src\\output.txt", false));
        output.write("Memory Dump\n");
            output.write(var);
            output.close();
        
        s.execute();
    }

    static class Scheduler {

        ArrayList<process> p;

        Queue<process> priorityQueue;
        Queue<process> robinQueue;
        Queue<String> prioName;
        Queue<String> robinName;

        public Scheduler() {
            priorityQueue = new LinkedList<>();
            robinQueue = new LinkedList<>();
            prioName = new LinkedList<>();
            robinName = new LinkedList<>();

            p = new ArrayList<>();
        }

        public void addProcess(process p) {
            if (p.pcb.process_priority < 16 && p.pcb.process_priority > 0) {
                priorityQueue.add(p);
                prioName.add(p.pcb.process_file_name);
            } else if (p.pcb.process_priority < 32 && p.pcb.process_priority >= 16) {
                robinQueue.add(p);
                robinName.add(p.pcb.process_file_name);
            }
            this.p.add(p);
        }

        public void sortProcess(Queue<process> list) {
            GFG.sortQueue(list);
            //System.out.println(list);
        }
// assuming that every process arrive at time 0.
        public void execute() {
            
            sortProcess(priorityQueue);
            Queue<process> tempList = priorityQueue;
            int temp = priorityQueue.size();
            for (int i = 0; i < temp; i++) {
                process current = tempList.peek();
                //System.out.println(current.name);
                OS_Project.execute(current);

                tempList.poll();
            }
             tempList = robinQueue;
        temp = robinQueue.size();
        while (!tempList.isEmpty())
        {
            process current = tempList.peek();
            OS_Project.executeRobin(current);
            operations op = new operations();
            if((decimalToHex(current.pcb.y.spr[10]).equals("F3")) || operations.check.equals("overflow") || operations.check.equals("overflow"))
            {
                tempList.poll();
                System.out.println("Process Ended because F3 was read");
            }
            else
            {
                robinQueue.add(current);
                tempList.poll();
            }
        }
        }
    }
}

class GFG {

    public static int minIndex(Queue<process> list,
            int sortIndex) {
        int min_index = -1;
        int min_value = Integer.MAX_VALUE;
        int s = list.size();
        for (int i = 0; i < s; i++) {
            process current = list.peek();

            // This is dequeue() in Java STL
            list.poll();

            // we add the condition i <= sortIndex
            // because we don't want to traverse
            // on the sorted part of the queue,
            // which is the right part.
            if (current.pcb.process_priority <= min_value && i <= sortIndex) {
                min_index = i;
                min_value = current.pcb.process_priority;
            }
            list.add(current);
        }
        return min_index;
    }

    // Moves given minimum element 
    // to rear of queue
    public static void insertMinToRear(Queue<process> list,
            int min_index) {
        process min_process = null;
        int s = list.size();
        for (int i = 0; i < s; i++) {
            process current = list.peek();
            list.poll();
            if (i != min_index) {
                list.add(current);
            } else {
                min_process = current;
            }
        }
        list.add(min_process);
    }

    public static Queue<process> sortQueue(Queue<process> list) {
        for (int i = 1; i <= list.size(); i++) {
            int min_index = minIndex(list, list.size() - i);
            insertMinToRear(list, min_index);
        }
        return list;
    }

    //Driver function
//    public static void main(String[] args) {
//        Queue<Integer> list = new LinkedList<Integer>();
//        list.add(30);
//        list.add(11);
//        list.add(15);
//        list.add(4);
//
//        //Sort Queue
//        //sortQueue(list);
//        //print sorted Queue
//        while (list.isEmpty() == false) {
//            System.out.print(list.peek() + " ");
//            list.poll();
//        }
//    }
}
