import { Menu, Button } from '@mantine/core';
import { GameVersion } from '../../types';

export default function PatchMenu({
    gameVersions,
    gameVersionLoading,
    selectedPatch, 
    setSelectedPatch
}: {
    gameVersions: GameVersion[] | undefined;
    gameVersionLoading: boolean;
    selectedPatch: string;
    setSelectedPatch: (patch: string) => void;
}) {
    return (
        <Menu shadow="md" width={75}>
            <Menu.Target>
                <Button 
                    variant="outline" 
                    color='white' 
                    loading={gameVersionLoading}
                >
                    {selectedPatch}
                </Button>
            </Menu.Target>
            <Menu.Dropdown>
                {!gameVersionLoading && gameVersions && gameVersions.length === 0 && (
                    <Menu.Item disabled>
                        No patches available
                    </Menu.Item>
                )}
                {!gameVersionLoading && gameVersions && gameVersions.length > 0 && (
                    gameVersions.map((gameVersion) => (
                        <Menu.Item
                            key={gameVersion.id}
                            onClick={() => setSelectedPatch(gameVersion.patch)}
                        >
                            {gameVersion.patch}
                        </Menu.Item>
                    ))
                )}
            </Menu.Dropdown>
        </Menu>
    )
}
