import { useState } from 'react'
import { Role } from '../types';
import { Tooltip } from '@mantine/core';

interface RoleImageProps {
    role: Role;
    selected: Role;
    setSelected: (role: Role) => void;
    images: {
        inactive: string;
        active: string;
    };
}

export default function RoleImage({
    role,
    selected,
    setSelected,
    images
}: RoleImageProps) {
    const [isHovered, setIsHovered] = useState(false);

    const getSrc = () => {
        if (selected === role || isHovered) return images.active;
        return images.inactive;
    }

    const getRoleName = () => {
        switch (role) {
            case "TOP":
                return "Top";
            case "JUNGLE":
                return "Jungle";
            case "MIDDLE":
                return "Mid";
            case "BOTTOM":
                return "Bottom";
            case "UTILITY":
                return "Support";
            default:
                return "All";
        }
    }

    return (
        <Tooltip label={getRoleName()} withArrow>
            <img
                src={getSrc()}
                alt={role}
                className="w-8 h-8 cursor-pointer"
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                onClick={() => setSelected(role)}
            />
        </Tooltip>
    )
}
