package de.ellpeck.rockbottom.game.apiimpl;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ResourceName implements IResourceName{

    private String domain;
    private String resourceName;

    public ResourceName(String domain, String resourceName){
        this.set(domain, resourceName);
    }

    public ResourceName(String combined){
        if(combined.contains(Constants.RESOURCE_SEPARATOR)){
            String[] split = combined.split(Constants.RESOURCE_SEPARATOR);
            this.set(split[0], split[1]);
        }
        else{
            throw new IllegalArgumentException("Cannot create a resource name from combined string "+combined);
        }
    }

    private void set(String domain, String resourceName){
        this.domain = domain;
        this.resourceName = resourceName;
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
    public boolean isEmpty(){
        return this.toString().isEmpty();
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
}