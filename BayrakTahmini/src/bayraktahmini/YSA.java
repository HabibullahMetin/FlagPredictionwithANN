/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayraktahmini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author Habibullah
 */
    
public class YSA {
    
    private static final File dosya = new File(YSA.class.getResource("shuffled_dataset.txt").getPath());
    
    private double[] maksimumlar;
    private double[] minimumlar;
    private DataSet egitimVeriSeti = new DataSet(44, 8);
    private DataSet testVeriSeti = new DataSet(44, 8);
    private int araKatmanNoron;
    private final int epoch;
    MomentumBackpropagation bp;
    
    public YSA(int araKatmanNoron, double momentum, double lr, int epoch) throws FileNotFoundException, IOException{
        this.epoch = epoch;
        maksimumlar = new double[44];
        minimumlar =  new double[44];
        for (int i = 0; i < 8; i++) {
            maksimumlar[i] = Double.MIN_VALUE;
            minimumlar[i] = Double.MAX_VALUE;
        }
       veriSetiMaks();
       veriSetiAyristir();
        
        
        bp = new MomentumBackpropagation();
        bp.setMomentum(momentum);
        bp.setLearningRate(lr);
        bp.setMaxIterations(epoch);
        this.araKatmanNoron = araKatmanNoron;
    }
    
    
    private void veriSetiAyristir() throws FileNotFoundException, IOException {      
        // veri seti excelde RAND() fonksiyonuyla karıştırılmıştır ve 
        //veriSetiAyrıştır fonksiyonuyla test(%30) ve eğitim(%70) olarak ayrılmıştır.
        Scanner oku = new Scanner(dosya);
         int sayac = 0;
           
           while(oku.hasNextDouble()) {
               if(sayac < 135) {   // ilk 135 satır eğitim  için
                   double[] inputs = new double[44];
                   for (int i = 0; i < 44; i++) {
                   double d = oku.nextDouble();
                   inputs[i] = min_max(maksimumlar[i], minimumlar[i], d);
                }
               DataSetRow satir = new DataSetRow(inputs, new double[] {oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble()});
               egitimVeriSeti.add(satir);
                sayac++;
               }
               else {  // kalanı test için 
                   double[] inputs = new double[44];
                   for(int i = 0; i < 44; i++) {
                       double d = oku.nextDouble();
                       inputs[i] = min_max(maksimumlar[i], minimumlar[i], d);     
                   }
                   DataSetRow satir = new DataSetRow(inputs, new double[] {oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble(),oku.nextDouble()});
                   testVeriSeti.add(satir);
               }
           }

            oku.close();
    }
    private void veriSetiMaks() throws FileNotFoundException {
     Scanner scan = new Scanner(dosya);
        while(scan.hasNextDouble()){
            for (int i = 0; i < 44; i++) {
                double d = scan.nextDouble();
                if(d > maksimumlar[i])  maksimumlar[i] = d;
                if(d < minimumlar[i])  minimumlar[i] = d;
            }
            scan.nextDouble(); scan.nextDouble(); scan.nextDouble();
            scan.nextDouble(); scan.nextDouble(); scan.nextDouble();
                       scan.nextDouble(); scan.nextDouble(); 
        }
        scan.close();
    }
    
    private double min_max (double max, double min, double x){
        return  (x-min)/(max-min);
    }
    
   public void egit(double[] eldeEdilenHatalar) {
       MultiLayerPerceptron sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,44,araKatmanNoron,8);
       sinirselAg.setLearningRule(bp);
       //sinirselAg.learn(egitimVeriSeti);
       for (int i = 0; i < epoch; i++) {
           sinirselAg.learn(egitimVeriSeti);
           if(i == 0) {
               eldeEdilenHatalar[i] = 1;
           }
           else {
               eldeEdilenHatalar[i] = sinirselAg.getLearningRule().getPreviousEpochError();
           }
           
       }
       
       sinirselAg.save("ag.nnet");
       System.out.println("Eğitim tamamlandı.");
               
   }
   
   
   double mse(double[] beklenen , double[] cikti){
        double satirToplamHata = 0;
        for (int i = 0; i < 8; i++) {
            satirToplamHata += Math.pow((beklenen[i] - cikti[i]) , 2);
        }
        return satirToplamHata/8;
    }
   
   public double test() {
       NeuralNetwork sinirselAg = new NeuralNetwork().createFromFile("ag.nnet");
       double toplamHata = 0;
       for(DataSetRow dr : testVeriSeti) {
           sinirselAg.setInput(dr.getInput());
           sinirselAg.calculate();
           toplamHata += mse(dr.getDesiredOutput(), sinirselAg.getOutput());
       }
       return toplamHata / testVeriSeti.size();
   }
   
   public String tekTest(double[] inputs) {
       for (int i = 0; i < 44; i++) {
            inputs[i] = min_max(maksimumlar[i], minimumlar[i], inputs[i]);
        }
        NeuralNetwork sinirselAg = new NeuralNetwork().createFromFile("ag.nnet");
        sinirselAg.setInput(inputs);
        sinirselAg.calculate();
        double[] outputs = sinirselAg.getOutput();
        double[] roundedOutput = new double[8];
        for (int i = 0; i < 8; i++) {
            roundedOutput[i] = Math.round(outputs[i]);
           
       }
        return sonuc(roundedOutput);
    }
   
    public double egitimSonHata(){
        return bp.getTotalNetworkError();
    }
    
    
    
    public String sonuc(double[] outputs) {
        if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Afghanistan";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Albania";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Algeria";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "American Samoa";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Andorra";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Angola";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Anguilla";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Antigua Barbuda";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Argentina";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Australia";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Austria";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Bahamas";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Bahrain";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Bangladesh";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Barbados";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Belgium";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Belize";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Benin";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Bermuda";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Bhutan";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Bolivia";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Botswana";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Brazil";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "British Virgin Isles";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Brunei";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Bulgaria";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Burkina";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Burma";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Burundi";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Cameroon";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Canada";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Cape Verde Islands";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Cayman Islands";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Central African Republic";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Chad";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Chile";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "China";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Colombia";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Comorro Islands";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Congo";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Cook Islands";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Costa Rica";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Cuba";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Cyprus";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Czechoslovakia";
        }
         else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Denmark";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Djibouti";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Dominica";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Dominican Republic";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Ecuador";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Egypt";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "El Salvador";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Equatorial Guinea";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Ethiopia";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Faeroes";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Falklands Malvinas";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Fiji";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Finland";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "France";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "French-Guiana";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "French-Polynesia";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Gabon";
        }
        else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Gambia";
        }
         else if(outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Germany DDR";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Germany FRG";  
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Ghana";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Gibraltar";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Greece";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Greenland";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Grenada";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Guam";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Guatemala";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Guinea";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Guinea Bissau";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Guyana";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Haiti";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Honduras";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Hong Kong";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Hungary";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Iceland";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "India";
        }
         else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Indonesia";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Iran";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Iraq";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Ireland";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Israel";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Italy";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Ivory Coast";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Jamaica";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Japan";
        }
         else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Jordan";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Kampuchea";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Kenya";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Kiribati";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Kuwait";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Laos";  // Burda kaldık1
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Lebanon";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Lesotho";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Liberia";
        }
         else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Libya";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Liechtenstein";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Luxembourg";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Malagasy";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Malawi";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Malaysia";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Maldive Islands";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Mali";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Malta";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Marianas";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Mauritania";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Mauritius";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Mexico";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Micronesia";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Monaco";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Mongolia";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Montserrat";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Morocco";
        }
         else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Mozambique";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Nauru";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Nepal";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Netherlands";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Netherlands Antilles";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "New Zealand";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Nicaragua";
        }
       else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Niger";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Nigeria";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Niue";
        }
        else if(outputs[0] == 0 && outputs[1] == 1 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "North Korea";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "North Yemen";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Norway";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Oman";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Pakistan";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Panama";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Papua-New-Guinea";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Parguay";
        }
         else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Peru";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Philippines";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Poland";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Portugal";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Puerto Rico";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Qatar";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Romania";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Rwanda";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "San Marino";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Sao Tome";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Saudi Arabia";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Senegal";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Seychelles";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Sierra Leone";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Singapore";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Soloman Islands";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Somalia";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "South-Africa";
        }
         else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "South Korea";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "South Yemen";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Spain";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Sri Lanka";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "St Helena";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "St Kitts Nevis";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "St Lucia";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "St Vincent";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Sudan";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Surinam";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Swaziland";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Sweden";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Switzerland";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Syria";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Taiwan";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Tanzania";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Thailand";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Togo";
        }
         else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Tonga";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Trinidad Tobago";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Tunisia";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Turkey";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 0 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Turks Cocos Islands";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Tuvalu";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "UAE";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Uganda";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "UK";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Uruguay";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "US Virgin Isles";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "USA";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 0 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "USSR";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Vanuatu";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 1) {
            return "Vatican City";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 0) {
            return "Venezuela";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 0 && outputs[6] == 1 && outputs[7] == 1) {
            return "Vietnam";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 0) {
            return "Western Samoa";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 0 && outputs[7] == 1) {
            return "Yugoslavia";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 0) {
            return "Zaire";
        }
        else if(outputs[0] == 1 && outputs[1] == 0 && outputs[2] == 1 && outputs[3] == 1 && outputs[4] == 1 && outputs[5] == 1 && outputs[6] == 1 && outputs[7] == 1) {
            return "Zambia";
        }
         else if(outputs[0] == 1 && outputs[1] == 1 && outputs[2] == 0 && outputs[3] == 0 && outputs[4] == 0 && outputs[5] == 0 && outputs[6] == 0 && outputs[7] == 0) {
            return "Zimbabwe";
        }
         else {
             return "Ülke bulunamadı.";
         }
   }
        
    
    
    
    
       
}
