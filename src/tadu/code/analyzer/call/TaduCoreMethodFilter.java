package tadu.code.analyzer.call;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.taduk.analyzer.util.ASTUtil;

public class TaduCoreMethodFilter {

	/*定义了哪些包下面的方法是可能出现在web层的调用中 */
	private static final String[] OUTER_PACKAGE_NAMES = new String[] {
			"com.tywire.tadu.facade", "com.tywire.tadu.util",
			"com.tywire.tadu.cache" };
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public static HashSet<IMethod> findOuterMethod(CalledMethodEntity entity) {
		HashSet<IMethod> outerMethods = new HashSet<IMethod>();
		try {
			findOuterMethodRecursive(entity, outerMethods);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
		}
		return outerMethods;
	}

	/**
	 * 找出最外层的方法
	 * TODO 还应该过滤掉非public的方法
	 * @param entity
	 * @param outerMethods
	 * @throws JavaModelException
	 */
	private static void findOuterMethodRecursive(CalledMethodEntity entity,
			HashSet<IMethod> outerMethods) throws JavaModelException {
		if (entity.getMethod() != null) {
			IType type = entity.getMethod().getCompilationUnit().getTypes()[0];
			String packageName = type.getPackageFragment().getElementName();
			for (String outerPackage : OUTER_PACKAGE_NAMES) {
				if(packageName.startsWith(outerPackage)) {
					/*只有public的方法*/
					if (ASTUtil.isPublicMethod(entity.getMethod())) {
						outerMethods.add(entity.getMethod());
					}
				}
			}
		}
		if (entity.getCalledMethodEntities() == null
				|| entity.getCalledMethodEntities().size() == 0) {
			return;
		}
		List<CalledMethodEntity> cmEntities = entity.getCalledMethodEntities();
		for (CalledMethodEntity cmEntity : cmEntities) {
			findOuterMethodRecursive(cmEntity, outerMethods);
		}
	}
	
	
	public static void main(String[] args) {
		
	}

}
