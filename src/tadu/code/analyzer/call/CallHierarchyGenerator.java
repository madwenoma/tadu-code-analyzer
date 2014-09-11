package tadu.code.analyzer.call;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

public class CallHierarchyGenerator {

	private static final String EXCLUDE_PACKAGE_NAME = "com.tywire.tadu.test";

	private static final String ANONY_START_STR = "<anonymous #1>";
	private static final String ANONY_START_CLASS_STR = "class <anonymous #1>";

	private final IJavaProject javaProject;

	public CallHierarchyGenerator(IJavaProject project) {
		this.javaProject = project;

	}

	public HashSet<IMethod> getCalledMethods(IMethod method) {

		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = {method};
		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IMethod> temp = getIMethods(mw2);
			callers.addAll(temp);
		}

		return callers;
	}

	private HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> c = new HashSet<IMethod>();
		for (MethodWrapper m : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	private IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) m.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public IMethod findMethod(IType type, String methodName) throws JavaModelException {
		if (type == null)
			return null;
		IMethod[] methods = type.getMethods();
		IMethod theMethod = null;

		for (int i = 0; i < methods.length; i++) {
			IMethod imethod = methods[i];
			if (imethod.getElementName().equals(methodName)) {
				theMethod = imethod;
			}
		}

		if (theMethod == null) {
			System.out.println("Error, method" + methodName + " not found");
			return null;
		}

		return theMethod;
	}

	// void referWorld() {key=Lcom/prosseek/test/ReferenceHello;.referWorld()V}
	// [in ReferenceHello [in ReferenceHello.java [in com.prosseek.test [in src
	// [in javaTest]]]]]
	// void referWorld() {key=Lcom/prosseek/test/Hello;.referWorld()V} [in Hello
	// [in Hello.java [in com.prosseek.test [in src [in javaTest]]]]]

	/**
	 * 递归查找方法调用，返回树形对象 m : Mapper getBook
	 * 
	 * @throws JavaModelException
	 */
	public void getCallersRecursive(IMethod method, CalledMethodEntity calledTree) throws JavaModelException {

		if (isTaduAnonymousMethod(method))// 递归终止条件
			return;

		HashSet<IMethod> calledMethods = getCalledMethods(method);

		if (calledMethods.size() == 0) { // 递归终止条件
			calledTree.setMethod(method); //TODO 20140420修改未测
			return;
		}
		calledTree.setMethod(method);

		ArrayList<CalledMethodEntity> calledMethodEntities = new ArrayList<CalledMethodEntity>();

		CalledMethodEntity entity; // mapper getBook
		for (IMethod calledMethod : calledMethods) {
			ICompilationUnit cUnit = calledMethod.getCompilationUnit();
			if (cUnit != null && cUnit.getParent() instanceof PackageFragment) {

				String[] packageNames = ((PackageFragment) cUnit.getParent()).names;
				String packageName = "";
				for (String temp : packageNames) {
					packageName += temp + ".";
				}
				if (packageName.startsWith(EXCLUDE_PACKAGE_NAME)) {
					continue;
				}
			}
			entity = new CalledMethodEntity();
			entity.setMethod(calledMethod);
			calledMethodEntities.add(entity);
		}
		calledTree.setCalledMethodEntities(calledMethodEntities);

		for (CalledMethodEntity calledMethodEntity : calledMethodEntities) {
			IMethod calledMethod = calledMethodEntity.getMethod();
			if (isAnonymousMethod(calledMethod)) {
				// TODO
				calledMethodEntity.setMethod((this.findMethod(calledMethod.getCompilationUnit().getTypes()[0],
						calledMethod.getParent().getParent().getElementName())));
			}
			getCallersRecursive(calledMethodEntity.getMethod(), calledMethodEntity);
		}
	}

	/**
	 * @param classWithPkgName 形如：com.tywire.tadu.dao.BookTopicSlaveMapper
	 * @param methodName 形如：getBookTopicById
	 * @return
	 */
	public CalledMethodEntity getCallersRecursive(String classWithPkgName, String methodName) {
		CalledMethodEntity entity = new CalledMethodEntity();
		try {
			IType type = javaProject.findType(classWithPkgName);
			IMethod method = this.findMethod(type, methodName);
			if (method != null)
				getCallersRecursive(this.findMethod(type, methodName), entity);
			if (entity.getMethod() == null) {

			}
			return entity;
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;
	}

	/**
	 * 递归查找方法调用的终止条件，某些方法如果不需要继续查找，如call等
	 * 
	 * @param method
	 * @return
	 */
	private boolean isTaduAnonymousMethod(IMethod method) {
		if (method == null) {
			return false;
		}
		String methodName = method.getElementName().toString();
		return "call".equals(methodName) || "run".equals(methodName) || "doJob".equals(methodName);
	}

	/**
	 * 递归查找方法调用的终止条件，某些方法如果不需要继续查找，如call等
	 * 
	 * @param method
	 * @return
	 */
	private boolean isAnonymousMethod(IMethod method) {
		if (method == null) {
			return false;
		}
		String methodParentStr = method.getParent().toString();
		return methodParentStr.startsWith(ANONY_START_STR) || methodParentStr.startsWith(ANONY_START_CLASS_STR);
	}

}