package de.ellpeck.rockbottom.apiimpl;

import com.google.common.base.Preconditions;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ResourceName implements IResourceName{

    private final String domain;
    private final String resourceName;

    public ResourceName(String domain, String resourceName){
        this.domain = domain;
        this.resourceName = resourceName;
    }

    public ResourceName(String combined){
        Preconditions.checkArgument(Util.isResourceName(combined), "Cannot create a resource name from combined string "+combined);

        String[] split = combined.split(Constants.RESOURCE_SEPARATOR, 2);
        this.domain = split[0];
        this.resourceName = split[1];
    }

    @Override
    public String getDomain(){
        return this.domain;
    }

    @Override
    public String getResourceName(){
        return this.resourceName;
    }

    public String toString(){
        return this.getDomain()+Constants.RESOURCE_SEPARATOR+this.getResourceName();
    }

    @Override
    public IResourceName addPrefix(String prefix){
        return new ResourceName(this.domain, prefix+this.resourceName);
    }

    @Override
    public IResourceName addSuffix(String suffix){
        return new ResourceName(this.domain, this.resourceName+suffix);
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        ResourceName name = (ResourceName)o;
        return this.domain.equals(name.domain) && this.resourceName.equals(name.resourceName);
    }

    @Override
    public int hashCode(){
        int result = this.domain.hashCode();
        result = 31*result+this.resourceName.hashCode();
        return result;
    }

    @Override
    public int compareTo(IResourceName o){
        return this.toString().compareTo(o.toString());
    }
}
