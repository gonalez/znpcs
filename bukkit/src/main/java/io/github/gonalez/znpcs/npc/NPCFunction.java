package io.github.gonalez.znpcs.npc;

public abstract class NPCFunction {
  private final String name;
  
  public enum ResultType {
   SUCCESS, FAIL
  }
  
  public NPCFunction(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  protected abstract boolean allow(NPC paramNPC);
  
  protected abstract ResultType runFunction(NPC paramNPC, FunctionContext paramFunctionContext);
  
  public void doRunFunction(NPC npc, FunctionContext functionContext) {
    if (!allow(npc))
      return; 
    ResultType resultType = runFunction(npc, functionContext);
    if (resultType == ResultType.SUCCESS)
      npc.getNpcPojo().getFunctions().put(getName(), !isTrue(npc));
  }
  
  protected ResultType resolve(NPC npc) {
    throw new IllegalStateException("resolve is not implemented.");
  }
  
  public boolean isTrue(NPC npc) {
    return FunctionFactory.isTrue(npc, this);
  }
  
  public static class WithoutFunction extends NPCFunction {
    public WithoutFunction(String name) {
      super(name);
    }
    
    protected ResultType runFunction(NPC npc, FunctionContext functionContext) {
      return ResultType.SUCCESS;
    }
    
    protected boolean allow(NPC npc) {
      return true;
    }
    
    protected ResultType resolve(NPC npc) {
      return ResultType.SUCCESS;
    }
  }
  
  public static class WithoutFunctionSelfUpdate extends WithoutFunction {
    public WithoutFunctionSelfUpdate(String name) {
      super(name);
    }
    
    protected ResultType runFunction(NPC npc, FunctionContext functionContext) {
      npc.deleteViewers();
      return ResultType.SUCCESS;
    }
  }
}
