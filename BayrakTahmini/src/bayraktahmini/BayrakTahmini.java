/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayraktahmini;

import java.io.IOException;
import java.util.Scanner;


/**
 *
 * @author Habibullah
 */
public class BayrakTahmini {


    public static void main(String[] args) throws IOException {
        
        
        YSA ann = null;
        
        Scanner scan = new Scanner(System.in);
        int secim;
        
        do {     
            System.out.println(" 1- Ağı Eğit");
            System.out.println(" 2- Ağı Test Et");
            System.out.println(" 3- Tek Veri ile Test Et");
            System.out.println(" 4- Çıkış");
            System.out.println("Seçiminiz =>");
            secim = scan.nextInt();
            switch(secim) {
                case 1 :
                     ann = new YSA(88,0.6,0.01,100);      
                    double[] errors = new double[100]; // epoch sayısı kadar büyüklükte
                    ann.egit(errors);
                    System.out.println("Eğitimdeki son hata değeri : " + ann.egitimSonHata());
                    break;
                case 2 :
                    System.out.println("Testteki hata değeri :" + ann.test());
                    break;
                case 3 :
                    double[] inputs = new double[44];
                    if(ann == null)
                    {
                        System.out.println("Önce Eğitim");
                        System.in.read();
                    }
                    else {
                        System.out.println("Kıta : (KuzeyAmerika - GüneyAmerika - Avrupa - Afrika - Asya - Okyanusya )");
                    String kita = scan.next();
                    if(kita.equals("KuzeyAmerika")) {
                        inputs[0] = 0; inputs[1] = 0; inputs[2] = 1;
                    }
                    if(kita.equals("GüneyAmerika")) {
                        inputs[0] = 0; inputs[1] = 1; inputs[2] = 0;
                    }
                    if(kita.equals("Avrupa")) {
                        inputs[0] = 0; inputs[1] = 1; inputs[2] = 1;
                    }
                    if(kita.equals("Afrika")) {
                        inputs[0] = 1; inputs[1] = 0; inputs[2] = 0;
                    }
                    if(kita.equals("Asya")) {
                        inputs[0] = 1; inputs[1] = 0; inputs[2] = 1;
                    }
                    if(kita.equals("Okyanusya")) {
                        inputs[0] = 1; inputs[1] = 1; inputs[2] = 0;
                    }
                    System.out.println("Bölge : (kuzeydoğu - güneydoğu - güneybatı - kuzeybatı)");
                    String zone = scan.next();
                    if(zone.equals("kuzeydoğu")) {
                        inputs[3] = 0; inputs[4] = 0; inputs[5] = 1;
                    }
                    if(zone.equals("güneydoğu")) {
                        inputs[3] = 0; inputs[4] = 1; inputs[5] = 0;
                    }
                    if(zone.equals("güneybatı")) {
                        inputs[3] = 0; inputs[4] = 1; inputs[5] = 1;
                    }
                    if(zone.equals("kuzeybatı")) {
                        inputs[3] = 1; inputs[4] = 0; inputs[5] = 0;
                    }
                     System.out.println("Kapladığı Alan(1000km2 cinsinden) (0-22402) : ");
                     inputs[6] = scan.nextDouble();
                     System.out.println("Nüfus(milyon cinsinden) (0-1008) : ");
                    inputs[7] = scan.nextDouble();
                     System.out.println("Dil : (İngilizce - İspanyolca - Fransızca - Almanca - Slavdili - DiğerHint-Avrupadilleri - Çince - Arapça - (Japonca - Türkçe - Fince - Macarca'dan biri) - Diğerdiller) )");
                     String dil = scan.next();
                     if(dil.equals("İngilizce")) {
                        inputs[8] = 0; inputs[9] = 0; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("İspanyolca")) {
                        inputs[8] = 0; inputs[9] = 0; inputs[10] = 1; inputs[11] = 0;
                    }
                    if(dil.equals("Fransızca")) {
                        inputs[8] = 0; inputs[9] = 0; inputs[10] = 1; inputs[11] = 1;
                    }
                    if(dil.equals("Almanca")) {
                       inputs[8] = 0; inputs[9] = 1; inputs[10] = 0; inputs[11] = 0;
                    }
                    if(dil.equals("Slavdili")) {
                        inputs[8] = 0; inputs[9] = 1; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("DiğerHint-Avrupadilleri")) {
                        inputs[8] = 0; inputs[9] = 1; inputs[10] = 1; inputs[11] = 0;
                    }
                    if(dil.equals("Çince")) {
                        inputs[8] = 0; inputs[9] = 1; inputs[10] = 1; inputs[11] = 1;
                    }
                    if(dil.equals("Arapça")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 0; inputs[11] = 0;
                    }
                    if(dil.equals("Japonca")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("Türkçe")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("Fince")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("Macarca")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 0; inputs[11] = 1;
                    }
                    if(dil.equals("Diğerdiller")) {
                        inputs[8] = 1; inputs[9] = 0; inputs[10] = 1; inputs[11] = 0;
                    }
                    System.out.println("Din : ( Katholik - DiğerHristiyan - Müslüman - Budist - Hindu - Etnik - Marksist - Diğerleri )");
                    String din = scan.next();
                    if(din.equals("Katholik")) {
                        inputs[12] = 0; inputs[13] = 0; inputs[14] = 0;
                    }
                    if(din.equals("DiğerHristiyan")) {
                        inputs[12] = 0; inputs[13] = 0; inputs[14] = 1;
                    }
                    if(din.equals("Müslüman")) {
                        inputs[12] = 0; inputs[13] = 1; inputs[14] = 0;
                    }
                    if(din.equals("Budist")) {
                        inputs[12] = 0; inputs[13] = 1; inputs[14] = 1;
                    }
                    if(din.equals("Hindu")) {
                        inputs[12] = 1; inputs[13] = 0; inputs[14] = 0;
                    }
                    if(din.equals("Etnik")) {
                        inputs[12] = 1; inputs[13] = 0; inputs[14] = 1;
                    }
                    if(din.equals("Marksist")) {
                        inputs[12] = 1; inputs[13] = 1; inputs[14] = 0;
                    }
                    if(din.equals("Diğerleri")) {
                        inputs[12] = 1; inputs[13] = 1; inputs[14] = 1;
                    }
                    System.out.println("Dikey Çizgi Sayısı (0-5) : ");
                    inputs[15] = scan.nextDouble();
                    System.out.println("Yatay Çizgi Sayısı (0-14) : ");
                    inputs[16] = scan.nextDouble();
                    System.out.println("Renk Sayısı (1-8) : ");
                    inputs[17] = scan.nextDouble();
                    System.out.println("Kırmızı renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[18] = scan.nextDouble();
                    System.out.println("Yeşil renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[19] = scan.nextDouble();
                    System.out.println("Mavi renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[20] = scan.nextDouble();
                    System.out.println("Sarı ya da Altın Sarısı rengi var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[21] = scan.nextDouble();
                    System.out.println("Beyaz renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[22] = scan.nextDouble();
                    System.out.println("Siyah renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[23] = scan.nextDouble();
                    System.out.println("Turuncu renk var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[24] = scan.nextDouble();
                    System.out.println("Bayraktaki göze çarpan renk ne ?(ana renk) (Yeşil - Kırmızı - Mavi - Sarı - Beyaz - Turuncu - Siyah - Kahverengi) : ");
                    String anaRenk = scan.next();
                    if(anaRenk.equals("Yeşil")) {
                        inputs[25] = 0; inputs[26] = 0; inputs[27] = 0;
                    }
                    if(anaRenk.equals("Kırmızı")) {
                        inputs[25] = 0; inputs[26] = 0; inputs[27] = 1;
                    }
                    if(anaRenk.equals("Mavi")) {
                        inputs[25] = 0; inputs[26] = 1; inputs[27] = 0;
                    }
                    if(anaRenk.equals("Sarı")) {
                        inputs[25] = 0; inputs[26] = 1; inputs[27] = 1;
                    }
                    if(anaRenk.equals("Beyaz")) {
                        inputs[25] = 1; inputs[26] = 0; inputs[27] = 0;
                    }
                    if(anaRenk.equals("Turuncu")) {
                        inputs[25] = 1; inputs[26] = 0; inputs[27] = 1;
                    }
                    if(anaRenk.equals("Siyah")) {
                        inputs[25] = 1; inputs[26] = 1; inputs[27] = 0;
                    }
                    if(anaRenk.equals("Kahverengi")) {
                        inputs[25] = 1; inputs[26] = 1; inputs[27] = 1;
                    }
                    System.out.println("Bayraktaki Daire Sayısı (0-4) : ");
                    inputs[28] = scan.nextDouble();
                    System.out.println("Bayraktaki soldan sağa yukarıdan aşağıya(ikisi birlikte olacak şekilde) çizgi sayısı (sembol gösterimi : +) (0-4) : ");
                    inputs[29] = scan.nextDouble();
                    System.out.println("Bayraktaki diagonal çizgi  sayısı (sembol gösterimi : x)(0-1) : ");
                    inputs[30] = scan.nextDouble();
                    System.out.println("Bayraktaki çeyrek bölme sayısı(Örneğin Panama bayrağında 4'tür.) (0-4) : ");
                    inputs[31] = scan.nextDouble();
                    System.out.println("Bayraktaki güneş veya yıldız sembolü sayısı (0-50) : ");
                    inputs[32] = scan.nextDouble();
                    System.out.println("Bayrakta hilal şeklinde ay var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[33] = scan.nextDouble();
                    System.out.println("Bayrakta üçgen var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[34] = scan.nextDouble();
                    System.out.println("Bayrakta ikon(cansız varlık Örneğin :tekne) var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[35] = scan.nextDouble();
                    System.out.println("Bayrakta canlı varlık(Örneğin : ağaç,insan eli,karga) var mı var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[36] = scan.nextDouble();
                    System.out.println("Bayrakta yazı(motto,slogan vs) var mı ? (yoksa : 0 - varsa : 1) : ");
                    inputs[37] = scan.nextDouble();
                    System.out.println("Bayrağın sol üst köşesindeki renk ne ? (Yeşil - Kırmızı - Mavi - Sarı - Beyaz - Turuncu - Siyah) : ");
                    String solUstKoseRengi = scan.next();
                    if(solUstKoseRengi.equals("Yeşil")) {
                        inputs[38] = 0; inputs[39] = 0; inputs[40] = 0;
                    }
                    if(solUstKoseRengi.equals("Kırmızı")) {
                        inputs[38] = 0; inputs[39] = 0; inputs[40] = 1;
                    }
                    if(solUstKoseRengi.equals("Mavi")) {
                        inputs[38] = 0; inputs[39] = 1; inputs[40] = 0;
                    }
                    if(solUstKoseRengi.equals("Sarı")) {
                        inputs[38] = 0; inputs[39] = 1; inputs[40] = 1;
                    }
                    if(solUstKoseRengi.equals("Beyaz")) {
                        inputs[38] = 1; inputs[39] = 0; inputs[40] = 0;
                    }
                    if(solUstKoseRengi.equals("Turuncu")) {
                        inputs[38] = 1; inputs[39] = 0; inputs[40] = 1;
                    }
                    if(solUstKoseRengi.equals("Siyah")) {
                        inputs[38] = 1; inputs[39] = 1; inputs[40] = 0;
                    }
                    System.out.println("Bayrağın sağ alt köşesindeki renk ne ? (Yeşil - Kırmızı - Mavi - Sarı - Beyaz - Turuncu - Siyah - Kahverengi) : ");
                    String sagAltKoseRengi = scan.next();
                    if(sagAltKoseRengi.equals("Yeşil")) {
                        inputs[41] = 0; inputs[42] = 0; inputs[43] = 0;
                    }
                    if(sagAltKoseRengi.equals("Kırmızı")) {
                        inputs[41] = 0; inputs[42] = 0; inputs[43] = 1;
                    }
                    if(sagAltKoseRengi.equals("Mavi")) {
                        inputs[41] = 0; inputs[42] = 1; inputs[43] = 0;
                    }
                    if(sagAltKoseRengi.equals("Sarı")) {
                        inputs[41] = 0; inputs[42] = 1; inputs[43] = 1;
                    }
                    if(sagAltKoseRengi.equals("Beyaz")) {
                        inputs[41] = 1; inputs[42] = 0; inputs[43] = 0;
                    }
                    if(sagAltKoseRengi.equals("Turuncu")) {
                        inputs[41] = 1; inputs[42] = 0; inputs[43] = 1;
                    }
                    if(sagAltKoseRengi.equals("Siyah")) {
                        inputs[41] = 1; inputs[42] = 1; inputs[43] = 0;
                    }
                     if(sagAltKoseRengi.equals("Kahverengi")) {
                        inputs[41] = 1; inputs[42] = 1; inputs[43] = 1;
                    }
                     System.out.println("Ülke : "+ann.tekTest(inputs));
                     System.in.read();
                        
                    }
                     
                     break;
                     
            }
            
        } while (secim != 4);
        
        
        
        
    }
    
}
