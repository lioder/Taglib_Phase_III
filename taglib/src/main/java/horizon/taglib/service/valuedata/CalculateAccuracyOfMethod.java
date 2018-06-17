package horizon.taglib.service.valuedata;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * 计算方法的准确度
 */
@Service
public class CalculateAccuracyOfMethod {

    /**
     * 计算大数投票审核方法的准确度
     * @return
     */
    public double calculateAccuracyByPostWay(){
        try {
            InputStream inputStream = new FileInputStream("./taglib/src/main/resources/bluebirds/labels.yaml");
            Yaml yaml = new Yaml();
            Map<Integer,Object> object = (Map<Integer,Object>)yaml.load(inputStream);
            InputStream inputStream1 = new FileInputStream("./taglib/src/main/resources/bluebirds/gt.yaml");
            Map<Integer,Object> correct = (Map<Integer,Object>)yaml.load(inputStream1);//标准答案
            int correctNum = 0;
            for(Integer i : correct.keySet()){
                boolean correctJudge = (Boolean) correct.get(i);
                int trueSize = 0;
                int falseSize = 0;
                for(Integer key : object.keySet()){
                    boolean judge = ((Map<Integer,Boolean>)object.get(key)).get(i);
                    if(judge==true){
                        trueSize++;
                    }else{
                        falseSize++;
                    }
                }
                if((trueSize>falseSize&&correctJudge==true)||(trueSize<falseSize&&correctJudge==false)){
                    correctNum++;
                }
            }
            double res = (double)correctNum/(double)correct.keySet().size();
            return res;
        }catch (FileNotFoundException e){
            System.out.println("未找到文件");
            e.printStackTrace();
            return -1;
        }
    }

}
