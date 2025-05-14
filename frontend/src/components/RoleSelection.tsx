import { Role } from '../types';
import RoleImage from './RoleImage';

export default function RoleSelection({
    selectedRole, 
    setSelectedRole
}: {
    selectedRole: Role;
    setSelectedRole: (role: Role) => void;
}) {
    return (
        <div className='flex gap-4 py-4'>
            <RoleImage
                role="ALL"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-fill.png",
                    active: "/roles/active/role-fill.png"
                }}
            />
            <RoleImage
                role="TOP"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-top.png",
                    active: "/roles/active/role-top.png"
                }}
            />
            <RoleImage
                role="JUNGLE"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-jungle.png",
                    active: "/roles/active/role-jungle.png"
                }}
            />
            <RoleImage
                role="MIDDLE"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-mid.png",
                    active: "/roles/active/role-mid.png"
                }}
            />
            <RoleImage
                role="BOTTOM"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-bot.png",
                    active: "/roles/active/role-bot.png"
                }}
            />
            <RoleImage
                role="UTILITY"
                selected={selectedRole}
                setSelected={setSelectedRole}
                images={{
                    inactive: "/roles/inactive/role-support.png",
                    active: "/roles/active/role-support.png"
                }}
            />
        </div>
    )
}
