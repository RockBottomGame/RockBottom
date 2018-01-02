package de.ellpeck.rockbottom.apiimpl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.rockbottom.api.construction.resource.IResourceRegistry;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.ResInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ResourceRegistry implements IResourceRegistry{

    private final ListMultimap<String, ResInfo> resources = ArrayListMultimap.create();
    private final ListMultimap<ResInfo, String> resourceNames = ArrayListMultimap.create();

    @Override
    public String addResources(String name, ResInfo... resources){
        List<ResInfo> resList = this.resources.get(name);

        for(ResInfo res : resources){
            if(!resList.contains(res)){
                resList.add(res);
            }

            List<String> nameList = this.resourceNames.get(res);
            if(!nameList.contains(name)){
                nameList.add(name);
            }
        }

        RockBottomAPI.logger().config("Registered resources "+Arrays.toString(resources)+" for resource name "+name);
        return name;
    }

    @Override
    public List<ResInfo> getResources(String name){
        List<ResInfo> resources = this.resources.get(name);
        return resources == null ? Collections.emptyList() : Collections.unmodifiableList(resources);
    }

    @Override
    public List<String> getNames(ResInfo resource){
        List<String> names = this.resourceNames.get(resource);
        return names == null ? Collections.emptyList() : Collections.unmodifiableList(names);
    }

    @Override
    public Set<ResInfo> getAllResources(){
        return Collections.unmodifiableSet(this.resourceNames.keySet());
    }

    @Override
    public Set<String> getAllResourceNames(){
        return Collections.unmodifiableSet(this.resources.keySet());
    }
}
