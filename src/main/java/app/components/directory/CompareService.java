package app.components.directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CompareService {

    @Autowired
    private Environment env;

    public void execComparation(){
        System.out.println(env.getProperty("watch_directory.path"));

    }


}
